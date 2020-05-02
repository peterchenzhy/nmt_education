package com.nmt.education.service.course;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.Enums;
import com.nmt.education.pojo.dto.req.CourseRegisterReqDto;
import com.nmt.education.pojo.dto.req.CourseReqDto;
import com.nmt.education.pojo.dto.req.CourseSearchDto;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.vo.CourseDetailVo;
import com.nmt.education.service.CodeService;
import com.nmt.education.service.course.expense.CourseExpenseService;
import com.nmt.education.service.course.registeration.CourseRegistrationService;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import com.nmt.education.service.teacher.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private CourseRegistrationService courseRegistrationService;

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
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
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
    public PageInfo<CoursePo> search(CourseSearchDto dto) {
        if (Objects.isNull(dto)) {
            return new PageInfo();
        }
        return PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> {
            this.coursePoMapper.queryByDto(dto);
        });
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
                BeanUtils.copyProperties(dto, coursePo);
                coursePo.setOperator(operator);
                coursePo.setOperateTime(new Date());
                this.updateByPrimaryKeySelective(coursePo);
                break;
            default:
                log.error("次editflag无法识别" + dto.toString());
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
        po.setCode(codeService.generateNewCourseCode(dto.getCampus(), dto.getCourseSubject()));
        return po;
    }

    public int insertSelective(CoursePo record) {
        return coursePoMapper.insertSelective(record);
    }


    public CoursePo selectByPrimaryKey(Long id) {
        return coursePoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(CoursePo record) {
        return coursePoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<CoursePo> list) {
        return coursePoMapper.updateBatch(list);
    }


    public int updateBatchSelective(List<CoursePo> list) {
        return coursePoMapper.updateBatchSelective(list);
    }


    public int batchInsert(List<CoursePo> list) {
        return coursePoMapper.batchInsert(list);
    }


    public int insertOrUpdate(CoursePo record) {
        return coursePoMapper.insertOrUpdate(record);
    }


    public int insertOrUpdateSelective(CoursePo record) {
        return coursePoMapper.insertOrUpdateSelective(record);
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
    public CourseDetailVo detail(Long id) {
        CoursePo po = selectByPrimaryKey(id);
        Assert.notNull(po, "没有获取到课程信息，id:" + id);
        CourseDetailVo vo = new CourseDetailVo();
        BeanUtils.copyProperties(po, vo);
        vo.getCourseExpenseList().addAll(courseExpenseService.queryByCourseId(id));
        vo.getCourseScheduleList().addAll(courseScheduleService.queryByCourseId(id));
        if (Objects.nonNull(po.getTeacherId())) {
            vo.setTeacher(teacherService.detail(po.getTeacherId()));
        }
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
    public List<CoursePo> searchFuzzy(String name) {
        Assert.hasLength(name, "课程模糊搜索关键字不能为空");
        return this.coursePoMapper.queryFuzzy(name);
    }

}
