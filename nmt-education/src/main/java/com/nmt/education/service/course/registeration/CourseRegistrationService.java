package com.nmt.education.service.course.registeration;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.NumberUtil;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.dto.req.*;
import com.nmt.education.pojo.po.*;
import com.nmt.education.pojo.vo.*;
import com.nmt.education.service.CodeService;
import com.nmt.education.service.course.CourseService;
import com.nmt.education.service.course.registeration.summary.RegisterationSummaryService;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import com.nmt.education.service.student.StudentService;
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

@Service
@Slf4j
public class CourseRegistrationService {

    @Resource
    private CourseRegistrationPoMapper courseRegistrationPoMapper;
    @Autowired
    @Lazy
    private CourseRegistrationService self;

    @Autowired
    private RegistrationExpenseDetailService registrationExpenseDetailService;

    @Autowired
    private RegisterationSummaryService registerationSummaryService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CodeService codeService;
    @Autowired
    private CourseScheduleService courseScheduleService;

    private static Integer 普通单节费用 = 1;

    /**
     * 课程报名
     *
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 10:53
     */
    public void register(CourseRegisterReqDto dto, int updator) {
        registerCheck(dto);
        self.startRegisterTransaction(dto, updator);

    }

    @Transactional(rollbackFor = Exception.class)
    public void startRegisterTransaction(CourseRegisterReqDto dto, int updator) {
        //生成报名记录
        CourseRegistrationPo courseRegistrationPo;
        if (Enums.EditFlag.新增.getCode().equals(dto.getEditFlag())) {
            courseRegistrationPo = generateCourseRegistrationPo(dto, updator);
            this.insertSelective(courseRegistrationPo);
        } else {
            courseRegistrationPo = this.courseRegistrationPoMapper.selectByPrimaryKey(dto.getId());
        }
        Assert.isTrue(Objects.nonNull(courseRegistrationPo), "非新增报名时，报名信息不存在，学生：" + dto.getStudentId() +
                "课程：" + dto.getCourseId());

        //缴费记录明细
        generateRegisterExpenseDetail(dto.getRegisterExpenseDetail(), updator,
                courseRegistrationPo);

        //汇总课表
        registerationSummaryService.batchInsert(generateRegisterationSummary(dto, updator, courseRegistrationPo));
    }

    /**
     * 生成 报名信息汇总表
     * 用于统计课程消耗的
     *
     * @param dto
     * @param updator
     * @param courseRegistrationPo
     * @return java.util.List<com.nmt.education.pojo.po.RegisterationSummaryPo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 21:30
     */
    private List<RegisterationSummaryPo> generateRegisterationSummary(CourseRegisterReqDto dto, int updator, CourseRegistrationPo courseRegistrationPo) {
        Assert.isTrue(!CollectionUtils.isEmpty(dto.getCourseScheduleIds()), "报名时不存在上课信息");
        List<RegisterationSummaryPo> list = new ArrayList<>(dto.getCourseScheduleIds().size());
        List<Long> courseScheduleIds;
        if (Enums.EditFlag.新增.getCode().equals(dto.getEditFlag())) {
            courseScheduleIds = dto.getCourseScheduleIds();
        } else {
            List<Long> alreadyList =
                    registerationSummaryService.queryByRegisterId(dto.getId()).stream().map(e -> e.getCourseScheduleId()).collect(Collectors.toList());
            courseScheduleIds = (List<Long>) org.apache.commons.collections4.CollectionUtils.subtract(dto.getCourseScheduleIds(), alreadyList);
        }
        courseScheduleIds.stream().forEach(e -> {
            RegisterationSummaryPo registerationSummaryPo = registerationSummaryService.dto2po(dto, updator, courseRegistrationPo, e);
            list.add(registerationSummaryPo);
        });
        return list;
    }


    private void registerCheck(CourseRegisterReqDto dto) {
        Assert.notNull(studentService.selectByPrimaryKey(dto.getStudentId()), "学生信息不存在！id:" + dto.getStudentId());
        Assert.notNull(courseService.selectByPrimaryKey(dto.getCourseId()), "学生信息不存在！id:" + dto.getCourseId());
        Assert.notEmpty(dto.getCourseScheduleIds(), "报名课时必填！id:" + dto.getCourseId());
        if (Enums.EditFlag.新增.getCode().equals(dto.getEditFlag())) {
            Assert.isNull(queryByCourseStudent(dto.getCourseId(), dto.getStudentId()), "报名记录已经存在！id:" + dto.getCourseId());
        }
    }

    /**
     * 根据课程和学生查找报名记录
     *
     * @param courseId
     * @param studentId
     * @return com.nmt.education.pojo.po.CourseRegistrationPo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/8 22:38
     */
    public CourseRegistrationListVo queryByCourseStudent(Long courseId, Long studentId) {
        return this.courseRegistrationPoMapper.queryByCourseStudent(courseId, studentId);
    }

    private void generateRegisterExpenseDetail(List<RegisterExpenseDetailReqDto> expenseDetailList, int updator,
                                               CourseRegistrationPo courseRegistrationPo) {
        Assert.isTrue(!CollectionUtils.isEmpty(expenseDetailList), "报名时不存在费用信息");
        List<RegistrationExpenseDetailPo> addList = new ArrayList<>(expenseDetailList.size());
        List<RegistrationExpenseDetailPo> updateList =
                registrationExpenseDetailService.selectByIds(expenseDetailList.stream().filter(e -> Objects.nonNull(e.getId()) && e.getId() != -1)
                        .map(e -> e.getId()).collect(Collectors.toList()));

        expenseDetailList.stream().filter(e -> Enums.EditFlag.修改.getCode().equals(e.getEditFlag())).forEach(e -> {
            addList.add(registrationExpenseDetailService.dto2po(updator, courseRegistrationPo, e));
        });

        registrationExpenseDetailService.batchInsert(addList);
        registrationExpenseDetailService.updateBatch(updateList);

    }


    /**
     * 报名记录搜索
     *
     * @param dto
     * @param logInUser
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/5 15:45
     */
    public PageInfo<CourseRegistrationListVo> registerSearch(RegisterSearchReqDto dto, Integer logInUser) {
        return PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.courseRegistrationPoMapper.queryByDto(dto));
    }

    /**
     * 生成 报名记录po
     *
     * @param dto
     * @param updator
     * @return com.nmt.education.pojo.po.CourseRegistrationPo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 14:35
     */
    private CourseRegistrationPo generateCourseRegistrationPo(CourseRegisterReqDto dto, int updator) {

        CourseRegistrationPo courseRegistrationPo = new CourseRegistrationPo();
        courseRegistrationPo.setRegistrationNumber(codeService.generateNewRegistrationNumber(dto.getCampus()));
        courseRegistrationPo.setCourseId(dto.getCourseId());
        courseRegistrationPo.setStudentId(dto.getStudentId());
        courseRegistrationPo.setTimes(dto.getCourseScheduleIds().size());
        courseRegistrationPo.setRegistrationType(dto.getRegistrationType());
        courseRegistrationPo.setRegistrationStatus(dto.getRegistrationStatus());
        courseRegistrationPo.setFeeStatus(dto.getFeeStatus());
        courseRegistrationPo.setStatus(StatusEnum.VALID.getCode());
        courseRegistrationPo.setRemark(dto.getRemark());
        courseRegistrationPo.setCreator(updator);
        courseRegistrationPo.setCreateTime(new Date());
        courseRegistrationPo.setOperator(updator);
        courseRegistrationPo.setOperateTime(new Date());
        courseRegistrationPo.setTotalAmount(calculateTotalAmount(dto.getRegisterExpenseDetail()));
        courseRegistrationPo.setBalanceAmount(courseRegistrationPo.getTotalAmount());
        return courseRegistrationPo;
    }

    /**
     * 计算订单总额
     *
     * @param registerExpenseDetail
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 21:54
     */
    private String calculateTotalAmount(List<RegisterExpenseDetailReqDto> registerExpenseDetail) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal checkResult = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(registerExpenseDetail)) {
            for (RegisterExpenseDetailReqDto e : registerExpenseDetail) {
                result = result.add(NumberUtil.String2Dec(e.getAmount()));
                checkResult = checkResult.add(NumberUtil.String2Dec(e.getDiscount())
                        .multiply(NumberUtil.String2Dec(e.getPerAmount()))
                        .multiply(new BigDecimal(e.getCount())));
            }
        }
        Assert.isTrue(result.compareTo(checkResult) == 0, "费用金额，与计算金额不符");
        return result.toPlainString();
    }

    /**
     * 退费
     *
     * @param id
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 10:53
     */
    public void registerDel(Long id, int updator) {

        // TODO: 2020/5/2 逻辑有问题  待定

        CourseRegistrationPo courseRegistrationPo = selectByPrimaryKey(id);
        Assert.notNull(courseRegistrationPo, "报名信息不存在，id：" + id);
        CoursePo coursePo = courseService.selectByPrimaryKey(courseRegistrationPo.getCourseId());
        Assert.notNull(coursePo, "课程信息不存在，id：" + courseRegistrationPo.getCourseId());
        //无效报名记录

        //无效课程消耗

        //无效费用信息
    }


    public PageInfo<RegisterSummaryVo> registerSummary(RegisterSummarySearchDto dto, Integer logInUser) {
        if (Objects.nonNull(dto.getEndDate())) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        if (Objects.nonNull(dto.getRegisterEndDate())) {
            dto.setRegisterEndDate(DateUtil.parseCloseDate(dto.getRegisterEndDate()));
        }
        PageInfo<RegisterSummaryVo> pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> queryBySearchDto(dto));
        return pageInfo;
    }

    /**
     * 报名记录查询 ，课时消耗查询
     *
     * @param dto
     * @return java.util.List<com.nmt.education.pojo.vo.RegisterSummaryVo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/11 22:02
     */
    private List<RegisterSummaryVo> queryBySearchDto(RegisterSummarySearchDto dto) {

        return this.registerationSummaryService.queryBySearchDto(dto);
    }


    public int insertSelective(CourseRegistrationPo record) {
        return courseRegistrationPoMapper.insertSelective(record);
    }


    public CourseRegistrationPo selectByPrimaryKey(Long id) {
        return courseRegistrationPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(CourseRegistrationPo record) {
        return courseRegistrationPoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<CourseRegistrationPo> list) {
        return courseRegistrationPoMapper.updateBatch(list);
    }


    public int updateBatchSelective(List<CourseRegistrationPo> list) {
        return courseRegistrationPoMapper.updateBatchSelective(list);
    }


    public int batchInsert(List<CourseRegistrationPo> list) {
        return courseRegistrationPoMapper.batchInsert(list);
    }


    public int insertOrUpdate(CourseRegistrationPo record) {
        return courseRegistrationPoMapper.insertOrUpdate(record);
    }


    public int insertOrUpdateSelective(CourseRegistrationPo record) {
        return courseRegistrationPoMapper.insertOrUpdateSelective(record);
    }


    public CourseRegistrationVo registerDetail(Long id, Integer logInUser) {
        if (Objects.isNull(id)) {
            return null;
        }
        CourseRegistrationVo vo = this.courseRegistrationPoMapper.queryVoById(id);
        Assert.notNull(vo, "无法查询到报名记录，id:" + id);
        vo.setCourse(courseService.selectByPrimaryKey(vo.getCourseId()));
        vo.setStudent(studentService.detail(vo.getStudentId()));
        Map<Long, RegisterationSummaryPo> registerationSummaryMap =
                registerationSummaryService.queryByRegisterId(id).stream().collect(Collectors.toMap(k -> k.getCourseScheduleId(), v -> v));
        if (!CollectionUtils.isEmpty(registerationSummaryMap)) {
            vo.setCourseScheduleList(
                    courseScheduleService.queryByIds(new ArrayList<>(registerationSummaryMap.keySet()))
                            .stream().map(e -> {
                        StudentCourseScheduleSummaryVo v = new StudentCourseScheduleSummaryVo();
                        BeanUtils.copyProperties(e, v);
                        v.setStudentSignIn(registerationSummaryMap.get(v.getId()).getSignIn());
                        v.setRegisterSummaryId(registerationSummaryMap.get(v.getId()).getId());
                        return v;
                    }).collect(Collectors.toList())
            );
        }
        vo.setRegisterExpenseDetail(registrationExpenseDetailService.queryRegisterId(id));
        return vo;
    }

    /**
     * 根据课程id 查询学生报名情况
     *
     * @param courseId 课程id
     * @return java.util.List<com.nmt.education.pojo.vo.StudentVo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/14 23:36
     */
    public List<StudentVo> registerStudent(Long courseId) {
        List<CourseRegistrationPo> registrationList = this.courseRegistrationPoMapper.queryByCourseId(courseId);
        if (CollectionUtils.isEmpty(registrationList)) {
            return Collections.emptyList();
        }
        List<StudentPo> studentPoList = studentService.queryByIds(registrationList.stream().map(e -> e.getStudentId()).collect(Collectors.toList()));
        List<StudentVo> voList = new ArrayList<>(studentPoList.size());
        studentPoList.stream().forEach(e -> voList.add(studentService.po2vo(e)));
        return voList;
    }

    /**
     * 根据报名id 查询summary记录
     *
     * @param registerId
     * @param pageNo
     * @return com.github.pagehelper.PageInfo<com.nmt.education.pojo.vo.RegisterSummaryVo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/30 11:59
     */
    public PageInfo<RegisterationSummaryPo> registerSummaryByRegisterId(Long registerId, Integer loginUser, Integer pageNo, Integer pageSize) {

        return registerationSummaryService.queryPageByRegisterId(registerId, pageNo, pageSize);

    }

    /**
     * 处理单次报名退费
     *
     * @param dto
     * @param logInUser
     * @return void
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/6/5 22:37
     */
    @Transactional(rollbackFor = Exception.class)
    public void registerRefund(RefundReqDto dto, Integer logInUser) {
        List<RefundItemReqDto> dtoList = dto.getItemList();
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }
        List<RegistrationExpenseDetailPo> expenseDetailList = registrationExpenseDetailService.queryRegisterId(dto.getRegisterId());
        Map<Integer, RegistrationExpenseDetailPo> validExpenseDetail =
                expenseDetailList.stream().filter(e -> Enums.FeeStatus.已缴费.getCode().equals(e.getFeeStatus()) && Enums.FeeDirection.支付.getCode().equals(e.getFeeDirection()))
                        .collect(Collectors.toMap(k -> k.getFeeType(), v -> v));
        Map<Integer, List<RefundItemReqDto>> itemMap = dtoList.stream().collect(Collectors.groupingBy(e -> e.getFeeType()));
        itemMap.keySet().stream().forEach(k -> {
            if (k.equals(普通单节费用)) {
                RegisterationSummaryPo p = registerationSummaryService.selectByIds(itemMap.get(k).stream().map(e -> e.getRegisterSummaryId()).collect(Collectors.toList()))
                        .stream().filter(e -> !Enums.SignInType.canRefund.contains(e.getSignIn())).findAny().orElse(null);
                if (Objects.nonNull(p)) {
                    throw new RuntimeException("有记录已经被被退费了，RegisterationSummaryPoId:" + p.getId());
                }
                if (processRefund(dto, logInUser, itemMap.get(k), expenseDetailList, k)) {
                    //更新报名课表
                    registerationSummaryService.updateSignIn(itemMap.get(k).stream().map(e -> e.getRegisterSummaryId()).collect(Collectors.toList()),
                            logInUser, Enums.SignInType.已退费);
                }
            } else {
                if (processRefund(dto, logInUser, itemMap.get(k), expenseDetailList, k)) {
                    //将支付状态无效
                    RegistrationExpenseDetailPo expenseDetailPo = validExpenseDetail.get(k);
                    expenseDetailPo.setFeeStatus(Enums.FeeStatus.已退费.getCode());
                    expenseDetailPo.setOperateTime(new Date());
                    expenseDetailPo.setOperator(logInUser);
                    registrationExpenseDetailService.updateByPrimaryKeySelective(expenseDetailPo);
                }
            }
        });

    }

    /**
     * 处理退费
     *
     * @param dto
     * @param logInUser
     * @param itemList
     * @param expenseDetailList
     * @param feeType
     * @return 退费结果
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/6/6 14:38
     */
    private boolean processRefund(RefundReqDto dto, Integer logInUser, List<RefundItemReqDto> itemList
            , List<RegistrationExpenseDetailPo> expenseDetailList, Integer feeType) {
        if (CollectionUtils.isEmpty(itemList)) {
            log.warn("费用类型：{},没有项目明细，退费逻辑终止，dto：{}", feeType, dto);
            return false;
        }
        //获取付款记录
        List<RegistrationExpenseDetailPo> payList = expenseDetailList.stream().filter(
                e -> feeType.equals(e.getFeeType()) && Enums.FeeDirection.支付.getCode().equals(e.getFeeDirection())).collect(Collectors.toList());
        List<RegistrationExpenseDetailPo> refundedList = expenseDetailList.stream().filter(
                e -> feeType.equals(e.getFeeType()) && Enums.FeeDirection.退费.getCode().equals(e.getFeeDirection())).collect(Collectors.toList());
        //最大可退费金额
        BigDecimal maxCanRefund = BigDecimal.ZERO;
        for (RegistrationExpenseDetailPo e : payList) {
            maxCanRefund = maxCanRefund.add(NumberUtil.String2Dec(e.getAmount()));
        }
        for (RegistrationExpenseDetailPo e : refundedList) {
            maxCanRefund = maxCanRefund.subtract(NumberUtil.String2Dec(e.getAmount()));
        }
        BigDecimal applyRefund = NumberUtil.String2Dec(payList.get(0).getPerAmount())
                .multiply(NumberUtil.String2Dec(payList.get(0).getDiscount())).multiply(BigDecimal.valueOf(itemList.size()));
        Assert.isTrue(applyRefund.compareTo(maxCanRefund) <= 0, "退费金额不可大于最大可退费金额，" + maxCanRefund);
        //生成退费记录
        RegistrationExpenseDetailPo refundPo = new RegistrationExpenseDetailPo();
        refundPo.setRegistrationId(dto.getRegisterId());
        refundPo.setFeeType(itemList.get(0).getFeeType());
        refundPo.setFeeStatus(Enums.FeeStatus.已退费.getCode());
        refundPo.setAmount(applyRefund.toPlainString());
        refundPo.setPerAmount(payList.get(0).getPerAmount());
        refundPo.setCount(itemList.size());
        refundPo.setDiscount(payList.get(0).getDiscount());
        refundPo.setPayment(dto.getPayment());
        refundPo.setFeeDirection(Enums.FeeDirection.退费.getCode());
        refundPo.setStatus(StatusEnum.VALID.getCode());
        refundPo.setRemark(dto.getRemark());
        refundPo.setCreator(logInUser);
        refundPo.setCreateTime(new Date());
        refundPo.setOperator(logInUser);
        refundPo.setOperateTime(new Date());
        registrationExpenseDetailService.insertSelective(refundPo);
        return true;
    }

}
