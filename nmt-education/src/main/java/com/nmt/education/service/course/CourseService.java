package com.nmt.education.service.course;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.utils.SpringContextUtil;
import com.nmt.education.listener.event.BaseEvent;
import com.nmt.education.listener.event.TeacherChangeEvent;
import com.nmt.education.pojo.dto.req.CourseReqDto;
import com.nmt.education.pojo.dto.req.CourseScheduleReqDto;
import com.nmt.education.pojo.dto.req.CourseSearchDto;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.po.CourseSchedulePo;
import com.nmt.education.pojo.vo.CourseDetailVo;
import com.nmt.education.service.CodeService;
import com.nmt.education.service.campus.authorization.CampusAuthorizationService;
import com.nmt.education.service.course.expense.CourseExpenseService;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import com.nmt.education.service.student.account.StudentAccountService;
import com.nmt.education.service.teacher.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseService {

    @Autowired
    private CodeService codeService;
    @Autowired
    private CourseScheduleService courseScheduleService;
    @Autowired
    private CourseExpenseService courseExpenseService;
    @Resource
    private CoursePoMapper coursePoMapper;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private CampusAuthorizationService campusAuthorizationService;
    @Autowired
    private StudentAccountService studentAccountService;

    private final static ThreadLocal<List<BaseEvent>> eventList = ThreadLocal.withInitial(() -> new ArrayList<>(5));

    /**
     * 课程新增，修改，删除接口
     * 编辑标志；0：无变化，1：新增，2：编辑，3：需要删除
     *
     * @param loginUser
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 21:48
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean courseManager(Integer loginUser, CourseReqDto dto) {
        campusAuthorizationService.getCampusAuthorization(loginUser, dto.getCampus());
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        if (dto.getCourseExpenseList().stream().collect(Collectors.groupingBy(k -> k.getType())).values()
                .stream().filter(v -> v.size() > 1).findAny().isPresent()) {
            throw new RuntimeException("一个课程一个类型的费用配置只能有一个");
        }
        CoursePo po = null;
        switch (editFlag) {
            case 新增:
                po = newCourse(loginUser, dto);
                break;
            case 修改:
                po = editCourse(loginUser, dto);
                break;
            case 需要删除:
                po = editCourse(loginUser, dto);
                break;
            case 无变化:
                break;
            default:
                log.error("courseService请求数据不合规，无法辨认editFlag！" + dto);
                break;
        }
        if (Objects.nonNull(po)) {
            courseScheduleService.manager(dto.getCourseScheduleList(), po.getId(), loginUser);
            courseExpenseService.manager(dto.getCourseExpenseList(), po.getId(), loginUser);
        }
        eventList.get().stream().forEach(e -> SpringContextUtil.getApplicationContext().publishEvent(e));
        eventList.remove();
        return true;
    }


    /**
     * 课程搜索
     *
     * @param dto
     * @return com.github.pagehelper.PageInfo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/25 20:16
     */
    public PageInfo<CoursePo> search(int loginUserId, CourseSearchDto dto) {
        if (Objects.isNull(dto)) {
            return new PageInfo();
        }
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(loginUserId, dto.getCampus());
        Assert.isTrue(!CollectionUtils.isEmpty(campusList), "没有任何校区权限进行课程搜索");
        return PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.coursePoMapper.queryByDto(dto, campusList));
    }

    /**
     * 编辑课程
     *
     * @param operator
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @summary
     * @since 2020/4/11 22:34
     */
    private CoursePo editCourse(Integer operator, CourseReqDto dto) {
        Assert.notNull(dto.getId(), "课程id不存在");
        CoursePo coursePo = selectByPrimaryKey(dto.getId());
        Assert.notNull(coursePo, "课程信息不存在" + dto.getId());
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        switch (editFlag) {
            case 需要删除:
                invalidByPrimaryKey(coursePo.getId(), operator);
                break;
            case 修改:
                if (!coursePo.getTeacherId().equals(dto.getTeacherId())) {
                    eventList.get().add(new TeacherChangeEvent(coursePo.getId(), dto.getTeacherId()));
                }
                BeanUtils.copyProperties(dto, coursePo);
                coursePo.setTimes(dto.getCourseScheduleList().size());
                coursePo.setOperator(operator);
                coursePo.setOperateTime(new Date());
                this.updateByPrimaryKeySelective(coursePo);
                break;
            default:
                log.error("此editflag无法识别" + dto.toString());
                break;
        }
        return coursePo;
    }

    /**
     * 根据id 无效课程
     *
     * @param id
     * @param operator
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:33
     */
    private void invalidByPrimaryKey(Long id, Integer operator) {
        this.coursePoMapper.invalidByPrimaryKey(id, operator);
    }

    /**
     * 新增课程
     *
     * @param operator
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:08
     */
    private CoursePo newCourse(Integer operator, CourseReqDto dto) {
        CoursePo po = newCoursePo(operator, dto);
        this.coursePoMapper.insertSelective(po);
        return po;
    }

    /**
     * 创建新的coursePo
     *
     * @param operator
     * @param dto
     * @return com.nmt.education.pojo.po.CoursePo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:15
     */
    private CoursePo newCoursePo(Integer operator, CourseReqDto dto) {
        CoursePo po = new CoursePo();
        BeanUtils.copyProperties(dto, po);
        po.setCreator(operator);
        po.setCreateTime(new Date());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        po.setEndDate(dto.getCourseScheduleList().stream().sorted(Comparator.comparing(CourseScheduleReqDto::getCourseDatetime).reversed()).findFirst().get().getCourseDatetime());
        po.setCode(codeService.generateNewCourseCode(dto.getCampus(), dto.getCourseSubject()));
        return po;
    }


    public CoursePo selectByPrimaryKey(Long id) {
        return coursePoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(CoursePo record) {
        return coursePoMapper.updateByPrimaryKeySelective(record);
    }


    /**
     * 课程明细
     *
     * @param id 课程数据id
     * @return com.nmt.education.pojo.vo.CourseDetailVo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/25 20:47
     */
    public CourseDetailVo detail(Integer logInUser, Long id) {
        CoursePo po = selectByPrimaryKey(id);
        Assert.notNull(po, "没有获取到课程信息，id:" + id);
        campusAuthorizationService.getCampusAuthorization(logInUser, po.getCampus());
        CourseDetailVo vo = new CourseDetailVo();
        BeanUtils.copyProperties(po, vo);
        vo.getCourseExpenseList().addAll(courseExpenseService.queryByCourseId(id));
        vo.getCourseScheduleList().addAll(courseScheduleService.queryByCourseId(id));
        if (Objects.nonNull(po.getTeacherId()) && Consts.DEFAULT_LONG != po.getTeacherId()) {
            vo.setTeacher(teacherService.detail(po.getTeacherId()));
        }
        List<CourseSchedulePo> courseSchedulePoList = courseScheduleService.queryByCourseId(po.getId());
        vo.setCourseSchedule(courseSchedulePoList.stream().
                filter(e -> Enums.SignInType.已签到.getCode().equals(e.getSignIn())).sorted(Comparator.comparing(CourseSchedulePo::getId).reversed())
                .findFirst().orElse(null));
        return vo;
    }

    /**
     * 根据 课程编号或者 课程名称模糊搜索
     *
     * @param name
     * @return java.util.List<com.nmt.education.pojo.po.CoursePo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 9:08
     */
    public List<CoursePo> searchFuzzy(Integer logInUser, String name) {
        Assert.hasLength(name, "课程模糊搜索关键字不能为空");
        List<CoursePo> coursePoList = this.coursePoMapper.queryFuzzy(name);
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser);
        return coursePoList.stream().filter(e -> campusList.contains(e.getCampus())).collect(Collectors.toList());
    }

    /**
     * 自动调整课程状态-->未开课==>已开课
     *
     * @param courseId 课程状态id
     */
    public void adjustCourseStatus(long courseId) {
        final CourseSchedulePo courseSchedulePo = this.courseScheduleService.queryByCourseId(courseId)
                .stream().filter(e -> Enums.SignInType.已签到.getCode().equals(e.getSignIn())).findAny().orElse(null);
        final CoursePo coursePo = this.selectByPrimaryKey(courseId);
        if (Objects.nonNull(courseSchedulePo)) {
            if (Objects.nonNull(courseId)) {
                if (Enums.CourseStatus.未开课.getCode().equals(coursePo.getCourseStatus())) {
                    coursePo.setCourseStatus(Enums.CourseStatus.已开学.getCode());
                    coursePo.setOperateTime(new Date());
                    this.updateByPrimaryKeySelective(coursePo);
                }
            } else {
                log.warn("课程不存在，课程id:" + courseId);
            }
        } else {
            log.warn("课程未开始，课程id:" + courseId);
        }

    }


    /**
     * 结课逻辑
     *
     * @param logInUser
     * @param courseId
     */
    public void finish(Integer logInUser, Long courseId) {
        final CoursePo coursePo = this.coursePoMapper.selectByPrimaryKey(courseId);
        Assert.isTrue(Objects.nonNull(coursePo),"课程信息为空，id:"+courseId);
        Assert.isTrue(logInUser.equals(coursePo.getCreator()),"非次课程创建人不可结课");
        //结余数据增加
        studentAccountService.addByCourseFinish(logInUser,courseId);
        //更新课程状态
        coursePo.setCourseStatus(Enums.CourseStatus.已结课.getCode());
        coursePo.setOperateTime(new Date());
        coursePo.setOperator(logInUser);
    }
}
