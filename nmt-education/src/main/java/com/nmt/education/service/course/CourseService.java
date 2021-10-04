package com.nmt.education.service.course;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.ExpenseDetailFlowTypeEnum;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.commmons.utils.SpringContextUtil;
import com.nmt.education.listener.event.BaseEvent;
import com.nmt.education.listener.event.TeacherChangeEvent;
import com.nmt.education.pojo.dto.req.CourseReqDto;
import com.nmt.education.pojo.dto.req.CourseScheduleReqDto;
import com.nmt.education.pojo.dto.req.CourseSearchDto;
import com.nmt.education.pojo.po.*;
import com.nmt.education.pojo.vo.CourseDetailVo;
import com.nmt.education.pojo.vo.CourseVo;
import com.nmt.education.service.CodeService;
import com.nmt.education.service.authorization.AuthorizationCheckDto;
import com.nmt.education.service.authorization.AuthorizationDto;
import com.nmt.education.service.authorization.AuthorizationService;
import com.nmt.education.service.course.expense.CourseExpenseService;
import com.nmt.education.service.course.registeration.CourseRegistrationService;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailService;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import com.nmt.education.service.student.account.StudentAccountService;
import com.nmt.education.service.teacher.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.nmt.education.commmons.Consts.SYSTEM_USER;

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
    @Lazy
    private CourseService self;
    @Autowired
    private StudentAccountService studentAccountService;
    @Autowired
    @Lazy
    private CourseRegistrationService courseRegistrationService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    @Lazy
    private RegistrationExpenseDetailService registrationExpenseDetailService;

    private final static ThreadLocal<List<BaseEvent>> eventList = ThreadLocal.withInitial(() -> new ArrayList<>(5));

    private static String PROGRESS = "%s/%s";

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
//        campusAuthorizationService.getCampusAuthorization(loginUser, dto.getCampus());
        AuthorizationCheckDto checkDto = new AuthorizationCheckDto();
        checkDto.setUserId(loginUser);
        checkDto.setCampus(dto.getCampus());
        checkDto.setGrade(dto.getGrade());
        authorizationService.getAuthorization(checkDto);
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
    public PageInfo<CourseVo> search(int loginUserId, CourseSearchDto dto) {
        if (Objects.isNull(dto)) {
            return new PageInfo();
        }
//        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(loginUserId, dto.getCampus());
//        Assert.isTrue(!CollectionUtils.isEmpty(campusList), "没有任何校区权限进行课程搜索");

        AuthorizationCheckDto checkDto = new AuthorizationCheckDto();
        checkDto.setUserId(loginUserId);
        checkDto.setCampus(dto.getCampus());
        checkDto.setGrade(dto.getGrade());
        AuthorizationDto authorization = authorizationService.getAuthorization(checkDto);


        PageInfo<CoursePo> poPage = PageHelper.startPage(dto.getPageNo(), dto.getPageSize())
                .doSelectPageInfo(() -> getCoursePos(dto, authorization.getCampusList(), authorization.getGradeList()));
        if (CollectionUtils.isEmpty(poPage.getList())) {
            return new PageInfo<>();
        }
        PageInfo<CourseVo> voPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(poPage, voPageInfo);
        voPageInfo.setList(poPage.getList().stream().map(po -> {
            CourseVo vo = new CourseVo();
            BeanUtils.copyProperties(po, vo);
            return vo;
        }).collect(Collectors.toList()));
        //统计报名人数
        Map<Long, CourseRegisterCount> collect =
                courseRegistrationService.countStudentByCourse(voPageInfo.getList().stream().map(vo -> vo.getId()).collect(Collectors.toList()))
                        .stream().collect(Collectors.toMap(k -> k.getCourseId(), v -> v));
        //课程进展
        Map<Long, List<CourseSchedulePo>> courseScheduleMap =
                courseScheduleService.queryByCourseIds(voPageInfo.getList().stream().map(CoursePo::getId).collect(Collectors.toList()))
                        .stream().collect(Collectors.groupingBy(CourseSchedulePo::getCourseId));

        voPageInfo.getList().stream().forEach(v -> {
            CourseRegisterCount courseRegisterCount = collect.get(v.getId());
            if (Objects.nonNull(courseRegisterCount)) {
                v.setRegisterNum(courseRegisterCount.getCount());
            } else {
                v.setRegisterNum(0);
            }
            List<CourseSchedulePo> courseSchedulePoList = courseScheduleMap.get(v.getId());
            if (!CollectionUtils.isEmpty(courseSchedulePoList)) {
                long count = courseSchedulePoList.stream().filter(e -> e.getSignIn().equals(Enums.SignInType.已签到.getCode())).count();
                v.setProgress(String.format(PROGRESS, count, courseSchedulePoList.size()));
            }

        });
        return voPageInfo;

    }

    public List<CoursePo> getCoursePos(CourseSearchDto dto, List<Integer> campusList, List<Integer> gradeList) {
        return this.coursePoMapper.queryByDto(dto,
                campusList, gradeList);
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
        Assert.isTrue(!Enums.CourseStatus.已结课.getCode().equals(coursePo.getCourseStatus()) &&
                !Enums.CourseStatus.已取消.getCode().equals(coursePo.getCourseStatus()), "课程已经结课或者取消，无法再进行编辑");
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
                coursePo.setTimes(Math.toIntExact(dto.getCourseScheduleList().stream().filter(e -> !Enums.EditFlag.需要删除.getCode().equals(e.getEditFlag())).count()));
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
//        campusAuthorizationService.getCampusAuthorization(logInUser, po.getCampus());

        AuthorizationCheckDto reqDto = new AuthorizationCheckDto();
        reqDto.setUserId(logInUser);
        reqDto.setCampus(po.getCampus());
        reqDto.setGrade(po.getGrade());
        authorizationService.getAuthorization(reqDto);

        CourseDetailVo vo = new CourseDetailVo();
        BeanUtils.copyProperties(po, vo);
        vo.getCourseExpenseList().addAll(courseExpenseService.queryByCourseId(id));
        vo.getCourseScheduleList().addAll(courseScheduleService.queryByCourseId(id).stream().sorted(Comparator.comparing(CourseSchedulePo::getCourseDatetime)).collect(Collectors.toList()));
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
//        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser);
        AuthorizationDto authorization = authorizationService.getAuthorization(logInUser);
        return coursePoList.stream()
                .filter(e -> authorization.getCampusList().contains(e.getCampus())
                        && authorization.getGradeList().contains(e.getGrade())).collect(Collectors.toList());
    }

    /**
     * 自动调整课程状态-->未开课==>已开课
     *
     * @param courseId 课程状态id
     */
    public void adjustCourseStatus(long courseId) {
        final CoursePo coursePo = this.selectByPrimaryKey(courseId);
        if (Objects.isNull(coursePo)) {
            log.error("课程不存在，课程id:" + courseId);
            return;
        }
        if (!Enums.CourseStatus.未开课.getCode().equals(coursePo.getCourseStatus())) {
            return;
        }

        final CourseSchedulePo courseSchedulePo = this.courseScheduleService.queryByCourseId(courseId)
                .stream().filter(e -> Enums.SignInType.已签到.getCode().equals(e.getSignIn())).findAny().orElse(null);
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
        Assert.isTrue(Objects.nonNull(coursePo), "课程信息为空，id:" + courseId);
        Assert.isTrue(!Enums.CourseStatus.已结课.getCode().equals(coursePo.getCourseStatus()) &&
                !Enums.CourseStatus.结课中.getCode().equals(coursePo.getCourseStatus()) &&
                !Enums.CourseStatus.已取消.getCode().equals(coursePo.getCourseStatus()), "课程已经结课或者取消，无法再进行编辑");
        int i = this.coursePoMapper.setFinishing(courseId);
        Assert.isTrue(i > 0, "课程信息设置结算中状态失败，id:" + courseId);
        self.finishTrx(logInUser, coursePo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void finishTrx(Integer logInUser, CoursePo coursePo) {
        //结余数据增加
        studentAccountService.addByCourseFinish(logInUser, coursePo);
        //材料费消耗
        processOtherFee(logInUser, coursePo);


        //更新课程状态
        coursePo.setCourseStatus(Enums.CourseStatus.已结课.getCode());
        coursePo.setOperateTime(new Date());
        coursePo.setOperator(logInUser);
        this.updateByPrimaryKeySelective(coursePo);
    }

    /**
     * 处理其他费用
     *
     * @param logInUser
     * @param coursePo
     */
    private void processOtherFee(Integer logInUser, CoursePo coursePo) {
        List<CourseRegistrationPo> courseRegistrationPos = courseRegistrationService.queryByCourseId(coursePo.getId());
        Map<Long, CourseRegistrationPo> registrationPoMap = courseRegistrationPos.stream()
                .collect(Collectors.toMap(e -> e.getId(), v -> v, (v1, v2) -> v1));
        List<RegistrationExpenseDetailPo> expenseDetailPos =
                registrationExpenseDetailService.queryRegisterIds(courseRegistrationPos.stream().map(CourseRegistrationPo::getId).collect(Collectors.toList()))
                        .stream().filter(p -> !Consts.FEE_TYPE_普通单节费用.equals(p.getFeeType()) && Enums.FeeDirection.支付.getCode().equals(p.getFeeDirection()) &&
                        Enums.FeeStatus.已缴费.getCode().equals(p.getFeeStatus())).collect(Collectors.toList());
        List<CourseRegistrationPo> registrationUpdateList = new ArrayList<>();
        List<RegistrationExpenseDetailFlowPo> flowList = new ArrayList<>();
        for (RegistrationExpenseDetailPo expenseDetailPo : expenseDetailPos) {
            CourseRegistrationPo courseRegistrationPo = registrationPoMap.get(expenseDetailPo.getRegistrationId());
            BigDecimal balanceAmount = new BigDecimal(courseRegistrationPo.getBalanceAmount());
            courseRegistrationPo.setBalanceAmount(balanceAmount.subtract(new BigDecimal(expenseDetailPo.getAmount())).toPlainString());
            registrationUpdateList.add(courseRegistrationPo);
            RegistrationExpenseDetailFlowPo flow = new RegistrationExpenseDetailFlowPo();
            flow.setRegistrationId(courseRegistrationPo.getId());
            flow.setFeeType(expenseDetailPo.getFeeType());
            flow.setType(ExpenseDetailFlowTypeEnum.消耗.getCode());
            flow.setAmount(expenseDetailPo.getAmount());
            flow.setRegisterExpenseDetailId(expenseDetailPo.getId());
            flow.setStatus(StatusEnum.VALID.getCode());
            flow.setRemark("结余消耗");
            flow.setCreator(logInUser);
            flow.setCreateTime(new Date());
            flow.setOperator(logInUser);
            flow.setOperateTime(new Date());
            flow.setPerAmount(expenseDetailPo.getAmount());
            flow.setCount(1);
            flow.setDiscount(expenseDetailPo.getDiscount());
            flow.setPayment(SYSTEM_USER);
            flow.setAccountAmount(Consts.ZERO);
            flowList.add(flow);
        }
        courseRegistrationService.updateBatch(registrationUpdateList);
        registrationExpenseDetailService.batchInsertFlow(flowList);
    }


}
