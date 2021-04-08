package com.nmt.education.service.course.schedule;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.nmt.education.commmons.*;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.commmons.utils.RoleUtils;
import com.nmt.education.commmons.utils.SpringContextUtil;
import com.nmt.education.listener.event.CourseStatusChangeEvent;
import com.nmt.education.pojo.dto.req.CourseScheduleReqDto;
import com.nmt.education.pojo.dto.req.CourseSignInReqDto;
import com.nmt.education.pojo.dto.req.TeacherScheduleReqDto;
import com.nmt.education.pojo.po.*;
import com.nmt.education.pojo.vo.CourseSignInItem;
import com.nmt.education.pojo.vo.CourseSignInVo;
import com.nmt.education.pojo.vo.TeacherSalarySummaryDto;
import com.nmt.education.pojo.vo.TeacherScheduleDto;
import com.nmt.education.service.campus.authorization.CampusAuthorizationService;
import com.nmt.education.service.course.CourseService;
import com.nmt.education.service.course.registeration.CourseRegistrationService;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailService;
import com.nmt.education.service.course.registeration.summary.RegisterationSummaryService;
import com.nmt.education.service.sysconfig.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nmt.education.commmons.Consts.SYSTEM_USER;

@Service
@Slf4j
public class CourseScheduleService {

    @Resource
    private CourseSchedulePoMapper courseSchedulePoMapper;
    @Autowired
    @Lazy
    private CourseService courseService;
    @Autowired
    private RegisterationSummaryService registerationSummaryService;
    @Autowired
    private CourseRegistrationService courseRegistrationService;
    @Autowired
    private RegistrationExpenseDetailService registrationExpenseDetailService;
    @Autowired
    private CampusAuthorizationService campusAuthorizationService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    @Lazy
    private CourseScheduleService self ;

    public boolean manager(List<CourseScheduleReqDto> dtoList, Long courseId, Integer operator) {
        if (CollectionUtils.isEmpty(dtoList)) {
            invalidByCourseId(courseId, operator);
            return true;
        }
        dtoList.forEach(e -> manager(e, courseId, operator));

        //课程排序
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                List<CourseSchedulePo> list = queryByCourseId(courseId);
                if (CollectionUtils.isEmpty(list)) {
                    return;
                }
                List<CourseSchedulePo> newPoList = new ArrayList<>(list.size());
                List<CourseSchedulePo> sortList = list.stream()
                        .sorted(Comparator.comparing(CourseSchedulePo::getCourseDatetime)).collect(Collectors.toList());
                Map<Integer, CourseSchedulePo> idMap = list.stream().collect(Collectors.toMap(CourseSchedulePo::getCourseTimes, Function.identity()));
                int i = 1;
                int k = 1;
                for (CourseSchedulePo e : sortList) {
                    if (e.getCourseTimes() == i) {
                        i++;
                        k++;
                        continue;
                    }
                    //原来的时间被删了就是null
                    CourseSchedulePo courseSchedulePo = idMap.get(k);
                    while (courseSchedulePo == null) {
                        if (k > sortList.size()) {
                            log.error("不存在idMap数据：" + e);
                            break;
                        }
                        k = k + 1;
                        courseSchedulePo = idMap.get(k);
                    }
                    if (courseSchedulePo == null) {
                        break;
                    }

                    CourseSchedulePo newPo = new CourseSchedulePo();
                    BeanUtils.copyProperties(e, newPo);

                    newPo.setSignIn(courseSchedulePo.getSignIn());
                    newPo.setOperateTime(courseSchedulePo.getOperateTime());
                    newPo.setOperator(courseSchedulePo.getOperator());
                    newPo.setCourseTimes(i);
                    newPoList.add(newPo);
                    i++;
                    k++;
                }
                if (!CollectionUtils.isEmpty(newPoList)) {
                    log.info("调整课程编号，员工：[{}],数据:{}", operator, newPoList);
                    updateBatchSelective(newPoList);
                }

            }
        });
        return true;
    }

    private void invalidByCourseId(Long courseId, Integer operator) {
        this.courseSchedulePoMapper.invalidByCourseId(courseId, operator);
    }

    private void manager(CourseScheduleReqDto dto, Long courseId, Integer operator) {
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        List<CourseSchedulePo> addList = new ArrayList<>();
        List<CourseSchedulePo> editList = new ArrayList<>();
        switch (editFlag) {
            case 新增:
                addList.add(newSchedule(operator, dto, courseId));
                break;
            case 修改:
                editList.add(editSchedule(operator, dto));
                break;
            case 需要删除:
                editList.add(delSchedule(operator, dto));
                break;
            case 无变化:
                break;
            default:
                log.error("courseSchedule请求数据不合规，无法辨认editFlag！" + dto);
                break;
        }
        updateBatchSelective(editList);
        batchInsert(addList);
    }

    private CourseSchedulePo delSchedule(Integer operator, CourseScheduleReqDto dto) {
        CourseSchedulePo po = checkExist(dto);
        Assert.isTrue(po.getSignIn() == 0, "课表已经签到，无法进行删除,id:" + dto.getId());
        po.setStatus(StatusEnum.INVALID.getCode());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }

    private CourseSchedulePo editSchedule(Integer operator, CourseScheduleReqDto dto) {
        CourseSchedulePo po = checkExist(dto);
        int courseTimes = po.getCourseTimes();
        BeanUtils.copyProperties(dto, po);
        po.setCourseTimes(courseTimes);
        po.setStatus(StatusEnum.VALID.getCode());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }

    private CourseSchedulePo checkExist(CourseScheduleReqDto dto) {
        Assert.notNull(dto.getId(), "编辑课程信息缺少id");
        CourseSchedulePo po = selectByPrimaryKey(dto.getId());
        Assert.notNull(po, "课程信息不存在" + dto.getId());
        return po;
    }

    /**
     * 新增
     *
     * @param operator
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/24 10:54
     */
    private CourseSchedulePo newSchedule(Integer operator, CourseScheduleReqDto dto, Long courseId) {
        CourseSchedulePo po = new CourseSchedulePo();
        BeanUtils.copyProperties(dto, po);
        po.setCourseId(courseId);
        po.setStatus(StatusEnum.VALID.getCode());
        po.setCreator(operator);
        po.setCreateTime(new Date());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }


    public CourseSchedulePo selectByPrimaryKey(Long id) {
        return courseSchedulePoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(CourseSchedulePo record) {
        return courseSchedulePoMapper.updateByPrimaryKeySelective(record);
    }


    public void updateBatchSelective(List<CourseSchedulePo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        courseSchedulePoMapper.updateBatchSelective(list);
    }


    public void batchInsert(List<CourseSchedulePo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.stream().forEach(e -> {
            if (Objects.isNull(e.getTeacherId())) {
                e.setTeacherId(Consts.DEFAULT_LONG);
            }
        });
        courseSchedulePoMapper.batchInsert(list);
    }


    public List<CourseSchedulePo> queryByCourseId(Long id) {
        return this.courseSchedulePoMapper.queryByCourseId(id);
    }

    public List<CourseSchedulePo> queryByCourseIds(List<Long> ids) {
        return this.courseSchedulePoMapper.queryByCourseIds(ids);
    }

    /**
     * 签到v2
     *
     * @param courseSignInReqDto 数据
     * @param logInUser  登录人
     * @param roleId     角色
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2021/3/22 21:56
     */
    @Transactional(rollbackFor = Exception.class)
    public void signInV2(CourseSignInReqDto courseSignInReqDto, Integer logInUser, String roleId) {
        if(Objects.isNull(courseSignInReqDto)){
            return ;
        }
        RegisterationSummaryPo registerationSummaryPo = registerationSummaryService.selectByPrimaryKey(courseSignInReqDto.getRegisterSummaryId());
        Assert.notNull(registerationSummaryPo,"报名课时不能为空,"+courseSignInReqDto);
        CourseSignInItem signInItem = new CourseSignInItem();
        signInItem.setStudentId(registerationSummaryPo.getStudentId());
        signInItem.setStudentName("");
        signInItem.setSignIn(courseSignInReqDto.getSignIn());
        signInItem.setRegisterSummaryId(registerationSummaryPo.getId());
        signInItem.setCourseId(registerationSummaryPo.getCourseId());
        signInItem.setCourseScheduleId(registerationSummaryPo.getCourseScheduleId());
        signInItem.setSignInRemark("");

        self.doSignIn(Lists.newArrayList(signInItem), logInUser, roleId, registerationSummaryPo.getCourseScheduleId(), registerationSummaryPo.getCourseId());
    }

    /**
     * 签到
     * 只能对同一节课进行批量签到
     *
     * @param list
     * @param operator
     */
    @Transactional(rollbackFor = Exception.class)
    public void signIn(List<CourseSignInItem> list, Integer operator, String roleId) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Long courseScheduleId = list.get(0).getCourseScheduleId();
        Long courseId = list.get(0).getCourseId();

        self.doSignIn(list, operator, roleId, courseScheduleId, courseId);

    }
    @Transactional(rollbackFor = Exception.class)
    public void doSignIn(List<CourseSignInItem> list, Integer operator, String roleId, Long courseScheduleId, Long courseId) {
        CourseSchedulePo courseSchedulePo = selectByPrimaryKey(courseScheduleId);
        Assert.notNull(courseSchedulePo, "课表信息为空，id：" + courseScheduleId);

        CoursePo coursePo = courseService.selectByPrimaryKey(courseId);
        Assert.notNull(coursePo, "课程不能为空，id:" + courseId);

        if (!RoleUtils.is校长(roleId)) {
            Assert.isTrue(!Enums.CourseStatus.已结课.getCode().equals(coursePo.getCourseStatus()) &&
                    !Enums.CourseStatus.已取消.getCode().equals(coursePo.getCourseStatus()), "课程已经结课或者取消!");
        }

        List<RegistrationExpenseDetailFlowPo> flowList = new ArrayList<>(list.size());
        List<CourseRegistrationPo> courseRegistrationPoList = new ArrayList<>(list.size());
        List<CourseSignInItem> needUpdate = new ArrayList<>(list.size());
        //消耗记录
        for (CourseSignInItem courseSignInItem : list) {
            RegisterationSummaryPo registerationSummaryPo = registerationSummaryService.selectByPrimaryKey(courseSignInItem.getRegisterSummaryId());
            Assert.isTrue(Objects.nonNull(registerationSummaryPo), "报名课时不存在，id：" + courseSignInItem.getRegisterSummaryId());
            CourseRegistrationPo courseRegistrationPo =
                    courseRegistrationService.selectByPrimaryKey(registerationSummaryPo.getCourseRegistrationId());
            Assert.isTrue(Objects.nonNull(courseRegistrationPo), "报名记录不存在，id：" + registerationSummaryPo.getCourseRegistrationId());
            Enums.SignInType target = Enums.SignInType.codeOf(courseSignInItem.getSignIn());
            Enums.SignInType source = Enums.SignInType.codeOf(registerationSummaryPo.getSignIn());
            Boolean isConsumed = Enums.SignInType.isConsumed(source, target);
            if (Objects.isNull(isConsumed)) {
                //状态没有变化
                if (!target.equals(source)) {
                    needUpdate.add(courseSignInItem);
                    courseRegistrationPoList.add(courseRegistrationPo);
                }
                continue;
            } else {
                BigDecimal balanceAmount = new BigDecimal(courseRegistrationPo.getBalanceAmount());
                RegistrationExpenseDetailPo expenseDetailPo = registrationExpenseDetailService.queryRegisterId(courseRegistrationPo.getId())
                        .stream().filter(p -> Consts.FEE_TYPE_普通单节费用.equals(p.getFeeType()) && Enums.FeeDirection.支付.getCode().equals(p.getFeeDirection()) &&
                                Enums.FeeStatus.已缴费.getCode().equals(p.getFeeStatus())).findFirst().orElse(null);
                Assert.isTrue(Objects.nonNull(expenseDetailPo), "缴费记录不存在，registger_summary id：" + courseSignInItem.getRegisterSummaryId());
                BigDecimal perAmount = NumberUtil.mutify(expenseDetailPo.getPerAmount(), expenseDetailPo.getDiscount());
                //设置余额
                if (isConsumed) {
                    courseRegistrationPo.setBalanceAmount(balanceAmount.subtract(perAmount).toPlainString());
                    flowList.add(generateFlow(operator, courseRegistrationPo.getId(), expenseDetailPo, perAmount, ExpenseDetailFlowTypeEnum.消耗));
                } else {
                    courseRegistrationPo.setBalanceAmount(balanceAmount.add(perAmount).toPlainString());
                    flowList.add(generateFlow(operator, courseRegistrationPo.getId(), expenseDetailPo, perAmount, ExpenseDetailFlowTypeEnum.还原));
                }
                courseRegistrationPoList.add(courseRegistrationPo);
                needUpdate.add(courseSignInItem);
            }
        }

        //更新状态
        if (!CollectionUtils.isEmpty(needUpdate)) {
            registerationSummaryService.signIn(needUpdate, operator);
            //更新报名记录
            courseRegistrationService.updateBatch(courseRegistrationPoList);
            //插入流水
            registrationExpenseDetailService.batchInsertFlow(flowList);
        } else {
            log.info("课程签到没有状态变更，" + list);
        }


        //更新课程日历信息
        int signCount = registerationSummaryService.checkSignIn(courseSchedulePo.getId());
        if (signCount > 0) {
            if (!Enums.SignInType.已签到.getCode().equals(courseSchedulePo.getSignIn())) {
                courseSchedulePo.setSignIn(Enums.SignInType.已签到.getCode());
                courseSchedulePo.setOperator(operator);
                courseSchedulePo.setOperateTime(new Date());
                updateByPrimaryKeySelective(courseSchedulePo);
            }
        } else if (signCount == 0) {
            if (Enums.SignInType.已签到.getCode().equals(courseSchedulePo.getSignIn())) {
                courseSchedulePo.setSignIn(Enums.SignInType.未签到.getCode());
                courseSchedulePo.setOperator(operator);
                courseSchedulePo.setOperateTime(new Date());
                updateByPrimaryKeySelective(courseSchedulePo);
            }
        } else {
            throw new RuntimeException("registerationSummaryService.checkSignIn 异常，结果:" + signCount + "入参：" + courseSchedulePo);
        }

        //课程状态event
        SpringContextUtil.getApplicationContext().publishEvent(new CourseStatusChangeEvent(courseId));
    }

    /**
     * 生成流水
     *
     * @param operator
     * @param courseRegistrationId
     * @param perAmount
     * @param type
     * @return com.nmt.education.pojo.po.RegistrationExpenseDetailFlow
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/6/13 14:45
     */
    private RegistrationExpenseDetailFlowPo generateFlow(Integer operator, Long courseRegistrationId, RegistrationExpenseDetailPo registrationExpenseDetailPo,
                                                         BigDecimal perAmount,
                                                         ExpenseDetailFlowTypeEnum type) {
        RegistrationExpenseDetailFlowPo flow = new RegistrationExpenseDetailFlowPo();
        flow.setRegistrationId(courseRegistrationId);
        flow.setFeeType(Consts.FEE_TYPE_普通单节费用);
        flow.setType(type.getCode());
        flow.setAmount(perAmount.toPlainString());
        flow.setRegisterExpenseDetailId(registrationExpenseDetailPo.getId());
        flow.setStatus(StatusEnum.VALID.getCode());
        flow.setRemark(type.getDescription());
        flow.setCreator(operator);
        flow.setCreateTime(new Date());
        flow.setOperator(operator);
        flow.setOperateTime(new Date());
        flow.setPerAmount(perAmount.toPlainString());
        flow.setCount(1);
        flow.setDiscount(registrationExpenseDetailPo.getDiscount());
        flow.setPayment(SYSTEM_USER);
        flow.setAccountAmount(Consts.ZERO);
        return flow;
    }


    public void changeTeacher(long courseId, long newTeacherId) {
        CoursePo coursePo = courseService.selectByPrimaryKey(courseId);
        Assert.notNull(coursePo, "课程不存在，id：" + courseId);
        this.courseSchedulePoMapper.changeTeacher(courseId, newTeacherId);
    }

    /**
     * 根据ids 查询课程时间
     *
     * @param ids
     * @return java.util.List<com.nmt.education.pojo.po.CourseSchedulePo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/11 22:54
     */
    public List<CourseSchedulePo> queryByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return this.courseSchedulePoMapper.queryByIds(ids);
    }

    /**
     * 课程签到页面 select组件
     *
     * @param id        课程id
     * @param logInUser 登录人
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/28 22:18
     */
    public List<CourseSchedulePo> signInSelect(Long id, Integer logInUser) {
        CourseSchedulePo po = selectByPrimaryKey(id);
        Assert.notNull(po, "课表信息为空，id：" + id);
        return this.courseSchedulePoMapper.queryByCourseId(id);
    }

    /**
     * 课程签到页面数据
     *
     * @param courseScheduleId 课程日程id
     * @param logInUser        登录人
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/28 22:18
     */
    public List<CourseSignInItem> signInList(Long courseScheduleId, Integer logInUser) {
        return registerationSummaryService.queryByCourseScheduleId(courseScheduleId);
    }

    /**
     * 课程签到页面 --默认页面
     *
     * @param courseId  课程日程id
     * @param logInUser 登录人
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/28 22:18
     */
    @Deprecated
    public CourseSignInVo signInDefault(Long courseId, Integer logInUser) {
        CourseSignInVo vo = new CourseSignInVo();
        vo.setCourseId(courseId);
        CoursePo coursePo = courseService.selectByPrimaryKey(courseId);
        Assert.notNull(coursePo, "课程不存在，id：" + courseId);
        vo.setCourseName(coursePo.getName());
        List<CourseSchedulePo> courseSchedulePoList = courseSchedulePoMapper.queryByCourseId(courseId);
        vo.setCourseSchedule(courseSchedulePoList.stream().
                filter(e -> Enums.SignInType.已签到.getCode().equals(e.getSignIn())).sorted(Comparator.comparing(CourseSchedulePo::getId).reversed())
                .findFirst().orElse(null));
        vo.setSignInVos(Objects.nonNull(vo.getCourseSchedule()) ? signInList(vo.getCourseSchedule().getId(), SYSTEM_USER) : Collections.EMPTY_LIST);
        return vo;
    }

    /**
     * 课程明细统计
     *
     * @param dto
     * @param logInUser
     * @return
     */
    public List<TeacherScheduleDto> scheduleTeacherExportList(TeacherScheduleReqDto dto, Integer logInUser) {
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser, dto.getCampus());
        if (dto.getEndDate() != null) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        List<TeacherScheduleDto> resultList = new ArrayList<>(Consts.BATCH_100);
        dto.setPageNo(1);
        dto.setPageSize(Consts.BATCH_100);
        List<TeacherScheduleDto> dataList;
        do {
            dataList = getExportData(dto, campusList);
            int pageNo = dto.getPageNo() + 1;
            dto.setPageNo(pageNo);
            resultList.addAll(dataList);
        } while (!CollectionUtils.isEmpty(dataList));

        return resultList;
    }

    private List<TeacherScheduleDto> getExportData(TeacherScheduleReqDto dto, List<Integer> campusList) {
        PageInfo<TeacherScheduleDto> pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize(), false).doSelectPageInfo(() ->
                courseSchedulePoMapper.teacherSchedule(dto, campusList));
        pageInfo.getList().stream().forEach(e -> {
                    e.setCampusStr(configService.queryByTypeValue(SysConfigEnum.校区.getCode(), e.getCampus()).getDescription());
                    e.setSubject(configService.queryByTypeValue(SysConfigEnum.课程科目.getCode(), e.getCourseSubject()).getDescription());
                    e.setSignIn(Enums.SignInType.code2Desc(e.getSignInStatus()));
                    e.setGradeStr(configService.queryByTypeValue(SysConfigEnum.年级.getCode(), e.getGrade()).getDescription());
                    e.setStartDate(dto.getStartDate());
                    e.setEndDate(dto.getEndDate());
                }
        );
        return pageInfo.getList();
    }

    /**
     * 教师费用统计
     *
     * @param dto
     * @param logInUser
     * @return
     */
    public List<TeacherSalarySummaryDto> teacherSummaryExportList(TeacherScheduleReqDto dto, Integer logInUser) {

        //先获取数据范围
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser, dto.getCampus());
        if (dto.getEndDate() != null) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        dto.setPageNo(1);
        dto.setPageSize(Consts.BATCH_100);
        List<TeacherScheduleDto> dataList;
        Map<Long, Map<Long, TeacherSalarySummaryDto>> teacherMap = new HashMap<>();

        do {
            dataList = getExportData(dto, campusList);
            dataList.stream()
                    .collect(Collectors.groupingBy(TeacherScheduleDto::getTeacherId))
                    .forEach((k, v) -> {
                        if (teacherMap.get(k) == null) {
                            teacherMap.put(k, new HashMap<>());
                        }
                        Map<Long, TeacherSalarySummaryDto> dataMap = teacherMap.get(k);
                        v.stream().collect(Collectors.toMap(TeacherScheduleDto::getCourseId,
                                d -> getTeacherSalarySummaryDto(d),
                                (d1, d2) -> {
                                    d1.setTeacherPrice(
                                            NumberUtil.String2Dec(d1.getTeacherPrice()).add(NumberUtil.String2Dec(d2.getTeacherPrice())).toPlainString());
                                    d1.setTimes(d1.getTimes() + 1);
                                    return d1;
                                }
                        )).forEach((c, b) -> {
                            TeacherSalarySummaryDto bd = dataMap.get(c);
                            if (Objects.nonNull(bd)) {
                                bd.setTeacherPrice(
                                        NumberUtil.String2Dec(bd.getTeacherPrice()).add(NumberUtil.String2Dec(b.getTeacherPrice())).toPlainString());
                                bd.setTimes(bd.getTimes() + b.getTimes());
                            } else {
                                dataMap.put(c, b);
                            }

                        });
                    });

            int pageNo = dto.getPageNo() + 1;
            dto.setPageNo(pageNo);
        } while (!CollectionUtils.isEmpty(dataList));

        return teacherMap.values().stream().flatMap(dt -> dt.values().stream())
                .map(e -> {
                    e.setStartDate(dto.getStartDate());
                    e.setEndDate(dto.getEndDate());
                    return e;
                }).collect(Collectors.toList());
    }

    private TeacherSalarySummaryDto getTeacherSalarySummaryDto(TeacherScheduleDto d) {
        TeacherSalarySummaryDto dt = new TeacherSalarySummaryDto();
        dt.setCourseName(d.getCourseName());
        dt.setSubject(d.getSubject());
        dt.setTeacherName(d.getTeacherName());
        dt.setGradeStr(d.getGradeStr());
        dt.setTeacherPrice(d.getTeacherPrice());
        dt.setCampusStr(d.getCampusStr());
        dt.setCourseId(d.getCourseId());
        dt.setTeacherId(d.getTeacherId());
        dt.setCampus(d.getCampus());
        dt.setCourseSubject(d.getCourseSubject());
        dt.setGrade(d.getGrade());
        dt.setPerTime(d.getPerTime());
        dt.setTeacherPerPrice(d.getTeacherPrice());
        return dt;
    }

    //获取一段时间内 教师课时费
    public List<String> getTeacherPay(TeacherScheduleReqDto dto, Integer logInUser) {
        //先获取数据范围
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser, dto.getCampus());
        return this.courseSchedulePoMapper.getTeacherPay(dto, campusList);
    }


}
