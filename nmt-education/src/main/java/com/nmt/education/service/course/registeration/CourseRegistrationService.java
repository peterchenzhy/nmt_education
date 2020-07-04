package com.nmt.education.service.course.registeration;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.nmt.education.commmons.*;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.dto.req.*;
import com.nmt.education.pojo.po.*;
import com.nmt.education.pojo.vo.*;
import com.nmt.education.service.CodeService;
import com.nmt.education.service.campus.authorization.CampusAuthorizationService;
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
    @Autowired
    private CampusAuthorizationService campusAuthorizationService;


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
        registerCheck(updator, dto);
        self.startRegisterTransaction(dto, updator);

    }

    @Transactional(rollbackFor = Exception.class)
    public void startRegisterTransaction(CourseRegisterReqDto dto, int updator) {
        //生成报名记录
        CourseRegistrationPo courseRegistrationPo;
        if (Enums.EditFlag.新增.getCode().equals(dto.getEditFlag())) {
            Assert.notNull(dto.getRegisterExpenseDetail().stream().filter(e -> Consts.FEE_TYPE_普通单节费用.equals(e.getFeeType())).findAny().get(),
                    "报名必须含有单节收费项目");
            courseRegistrationPo = generateCourseRegistrationPo(dto, updator);
            this.insertSelective(courseRegistrationPo);
        } else {
            courseRegistrationPo = selectByPrimaryKey(dto.getId());
            courseRegistrationPo.setRemark(dto.getRemark());
            courseRegistrationPo.setOperator(updator);
            courseRegistrationPo.setOperateTime(new Date());
            BigDecimal prepTotal = new BigDecimal(courseRegistrationPo.getTotalAmount());
            BigDecimal prepBalance = new BigDecimal(courseRegistrationPo.getBalanceAmount());
            //计算增加金额
            BigDecimal total = BigDecimal.ZERO;
            int addTimes = 0;
            for (RegisterExpenseDetailReqDto e : dto.getRegisterExpenseDetail()) {
                total = total.add(new BigDecimal(e.getAmount()));
                if (Consts.FEE_TYPE_普通单节费用.equals(e.getFeeType())) {
                    addTimes = e.getCount();
                }
            }
            Assert.isTrue(total.compareTo(prepTotal) >= 0, "编辑金额不能小于原订单总额");
            courseRegistrationPo.setTotalAmount(calculateTotalAmount(dto.getRegisterExpenseDetail()));
            courseRegistrationPo.setBalanceAmount(prepBalance.add(total).subtract(prepTotal).toPlainString());
            courseRegistrationPo.setTimes(addTimes);
            this.updateByPrimaryKeySelective(courseRegistrationPo);
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
                    registerationSummaryService.queryByRegisterId(dto.getId()).stream()
                            .filter(a -> !Enums.SignInType.已退费.getCode().equals(a.getSignIn()))
                            .map(e -> e.getCourseScheduleId()).collect(Collectors.toList());
            courseScheduleIds = (List<Long>) org.apache.commons.collections4.CollectionUtils.subtract(dto.getCourseScheduleIds(), alreadyList);
        }
        courseScheduleIds.stream().forEach(e -> {
            RegisterationSummaryPo registerationSummaryPo = registerationSummaryService.dto2po(dto, updator, courseRegistrationPo, e);
            list.add(registerationSummaryPo);
        });
        return list;
    }


    private void registerCheck(int loginUserId, CourseRegisterReqDto dto) {
        Assert.notNull(studentService.selectByPrimaryKey(dto.getStudentId()), "学生信息不存在！id:" + dto.getStudentId());
        CoursePo coursePo = courseService.selectByPrimaryKey(dto.getCourseId());
        campusAuthorizationService.getCampusAuthorization(loginUserId, coursePo.getCampus());
        Assert.notNull(coursePo, "学生信息不存在！id:" + dto.getCourseId());
        Assert.notEmpty(dto.getCourseScheduleIds(), "报名课时必填！id:" + dto.getCourseId());
        if (Enums.EditFlag.新增.getCode().equals(dto.getEditFlag())) {
            CourseRegistrationListVo vo = queryByCourseStudent(dto.getCourseId(), dto.getStudentId());
            if (Objects.nonNull(vo)) {
                Assert.isTrue(Enums.RegistrationStatus.已退费.getCode().equals(vo.getRegistrationStatus()), "报名记录已经存在，不能重复报名！id：" + vo.getId());
            }
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
        List<RegistrationExpenseDetailFlow> flowList = new ArrayList<>(expenseDetailList.size());
        //处理修改记录
        Map<Long, RegisterExpenseDetailReqDto> reqDtoMap =
                expenseDetailList.stream().filter(e -> Enums.EditFlag.修改.getCode().equals(e.getEditFlag()))
                        .collect(Collectors.toMap(k -> k.getId(), v -> v));
        List<RegistrationExpenseDetailPo> updateList =
                registrationExpenseDetailService.selectByIds(new ArrayList<>(reqDtoMap.keySet()));
        updateList.stream().forEach(e -> {
            RegisterExpenseDetailReqDto dto = reqDtoMap.get(e.getId());
            e.setFeeType(dto.getFeeType());
            e.setFeeStatus(dto.getFeeStatus());
            e.setAmount(dto.getAmount());
            e.setPerAmount(dto.getPerAmount());
            e.setCount(dto.getCount());
            e.setDiscount(dto.getDiscount());
            e.setPayment(dto.getPayment());
            e.setRemark(Strings.nullToEmpty(dto.getRemark()));
            e.setOperator(updator);
            e.setOperateTime(new Date());
            flowList.add(generateFlow(updator, e, ExpenseDetailFlowTypeEnum.编辑));

        });

        //处理新增记录
        expenseDetailList.stream().filter(e -> Enums.EditFlag.新增.getCode().equals(e.getEditFlag())).forEach(e -> {
            addList.add(registrationExpenseDetailService.dto2po(updator, courseRegistrationPo, e));
        });

        registrationExpenseDetailService.batchInsert(addList);
        registrationExpenseDetailService.updateBatch(updateList);
        addList.stream().forEach(e -> flowList.add(generateFlow(updator, e, ExpenseDetailFlowTypeEnum.新增记录)));
        registrationExpenseDetailService.batchInsertFlow(flowList);

    }

    private RegistrationExpenseDetailFlow generateFlow(int updator, RegistrationExpenseDetailPo e, ExpenseDetailFlowTypeEnum type) {
        RegistrationExpenseDetailFlow flow = new RegistrationExpenseDetailFlow();
        flow.setRegistrationId(e.getRegistrationId());
        flow.setRegisterExpenseDetailId(e.getId());
        flow.setFeeType(e.getFeeType());
        flow.setType(type.getCode());
        flow.setAmount(e.getAmount());
        flow.setStatus(StatusEnum.VALID.getCode());
        flow.setRemark(type.getDescription());
        flow.setCreator(updator);
        flow.setCreateTime(new Date());
        flow.setOperator(updator);
        flow.setOperateTime(new Date());
        flow.setPerAmount(e.getPerAmount());
        flow.setCount(e.getCount());
        flow.setDiscount(e.getDiscount());
        flow.setPayment(e.getPayment());
        return flow;
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

    public PageInfo<RegisterSummaryVo> registerSummary(RegisterSummarySearchDto dto, Integer logInUser) {
        if (Objects.nonNull(dto.getEndDate())) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        if (Objects.nonNull(dto.getRegisterEndDate())) {
            dto.setRegisterEndDate(DateUtil.parseCloseDate(dto.getRegisterEndDate()));
        }
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser);
        PageInfo<RegisterSummaryVo> pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> queryBySearchDto(dto
                ,campusList));
        return pageInfo;
    }

    /**
     * 报名记录查询 ，课时消耗查询
     *
     * @param dto
     * @param campusList
     * @return java.util.List<com.nmt.education.pojo.vo.RegisterSummaryVo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/11 22:02
     */
    private List<RegisterSummaryVo> queryBySearchDto(RegisterSummarySearchDto dto, List<Integer> campusList) {

        return this.registerationSummaryService.queryBySearchDto(dto,campusList);
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


    /**
     * 报名记录详情
     *
     * @param id
     * @param logInUser
     * @return com.nmt.education.pojo.vo.CourseRegistrationVo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/6/13 16:08
     */
    public CourseRegistrationVo registerDetail(Long id, Integer logInUser) {
        if (Objects.isNull(id)) {
            return null;
        }
        CourseRegistrationVo vo = this.courseRegistrationPoMapper.queryVoById(id);
        Assert.notNull(vo, "无法查询到报名记录，id:" + id);
        vo.setCourse(courseService.selectByPrimaryKey(vo.getCourseId()));
        vo.setStudent(studentService.detail(vo.getStudentId()));
        Map<Long, RegisterationSummaryPo> registerationSummaryMap =
                registerationSummaryService.queryByRegisterId(id).stream()
                        .filter(e -> !Enums.SignInType.已退费.getCode().equals(e.getSignIn()))
                        .collect(Collectors.toMap(k -> k.getCourseScheduleId(),
                                v -> v));
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

    public void registerRefund(RefundReqDto dto, Integer logInUser) {
        CourseRegistrationPo courseRegistrationPo = courseRegistrationPoMapper.selectByPrimaryKey(dto.getRegisterId());
        Assert.isTrue(Objects.nonNull(courseRegistrationPo), "报名信息不存在，id：" + dto.getRegisterId());
        Assert.isTrue(!Enums.RegistrationStatus.已退费.getCode().equals(courseRegistrationPo.getRegistrationStatus()),
                "已退费的订单无法重复退费，id：" + dto.getRegisterId());
        List<RefundItemReqDto> dtoList = dto.getItemList();
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }
        List<RegistrationExpenseDetailPo> expenseDetailList = registrationExpenseDetailService.queryRegisterId(dto.getRegisterId());
        //按照费用类型分组
        Map<Integer, List<RefundItemReqDto>> itemMap = dtoList.stream().collect(Collectors.groupingBy(e -> e.getFeeType()));
        self.refundByFeeType(dto, logInUser, expenseDetailList, itemMap);

        BigDecimal refundTotal = BigDecimal.ZERO;
        int refundTimes = 0;
        for (RefundItemReqDto refundItemReqDto : dto.getItemList()) {
            refundTotal = refundTotal.add(new BigDecimal(refundItemReqDto.getAmount()));
            if (Consts.FEE_TYPE_普通单节费用.equals(refundItemReqDto.getFeeType())) {
                refundTimes++;
            }
        }
        //修改订单总额,报名次数
        courseRegistrationPo.setBalanceAmount(new BigDecimal(courseRegistrationPo.getBalanceAmount()).subtract(refundTotal).toPlainString());
        courseRegistrationPo.setTotalAmount(new BigDecimal(courseRegistrationPo.getTotalAmount()).subtract(refundTotal).toPlainString());
        courseRegistrationPo.setTimes(courseRegistrationPo.getTimes() - refundTimes);
        courseRegistrationPo.setOperateTime(new Date());
        courseRegistrationPo.setOperator(logInUser);

        //如果费用已经全部退完则更新 报名状态
        if (!registrationExpenseDetailService.queryRegisterId(dto.getRegisterId())
                .stream().map(e -> Enums.FeeStatus.已缴费.getCode().equals(e.getFeeStatus()) && Enums.FeeDirection.支付.getCode().equals(e.getFeeDirection()))
                .findAny().get()) {
            courseRegistrationPo.setRegistrationStatus(Enums.RegistrationStatus.已退费.getCode());
        }
        updateByPrimaryKeySelective(courseRegistrationPo);

    }

    /**
     * 根据费用类型退费
     *
     * @param dto
     * @param logInUser
     * @param expenseDetailList
     * @param itemMap
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/6/13 13:01
     */
    @Transactional(rollbackFor = Exception.class)
    public void refundByFeeType(RefundReqDto dto, Integer logInUser, List<RegistrationExpenseDetailPo> expenseDetailList, Map<Integer,
            List<RefundItemReqDto>> itemMap) {
        itemMap.keySet().stream().forEach(k -> {
            //按费用类型进行退款
            if (k.equals(Consts.FEE_TYPE_普通单节费用)) {
                //查询消耗情况
                List<RegisterationSummaryPo> registerationSummaryPoList = registerationSummaryService.queryByRegisterId(dto.getRegisterId());
                RegisterationSummaryPo p = registerationSummaryPoList.stream()
                        .filter(e -> itemMap.get(k).contains(e.getId()) && !Enums.SignInType.canRefund.contains(e.getSignIn())).findAny().orElse(null);
                if (Objects.nonNull(p)) {
                    throw new RuntimeException("有记录已经被被退费了，RegisterationSummaryPoId:" + p.getId());
                }
                if (processRefund(dto, logInUser, itemMap.get(k), expenseDetailList, k)) {
                    //更新报名课表
                    registerationSummaryService.updateSignIn(itemMap.get(k).stream().map(e -> e.getRegisterSummaryId()).collect(Collectors.toList()),
                            logInUser, Enums.SignInType.已退费, Enums.SignInType.已退费.getDesc());
                }
            } else {
                processRefund(dto, logInUser, itemMap.get(k), expenseDetailList, k);
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

        RegistrationExpenseDetailPo ref = payList.stream().filter(e -> Enums.FeeStatus.已缴费.getCode().equals(e.getFeeStatus())).findFirst().get();
        Assert.isTrue(Objects.nonNull(ref), "找不到支付关联信息，无法退费");

        //最大可退费金额
        BigDecimal maxCanRefund = BigDecimal.ZERO;
        for (RegistrationExpenseDetailPo e : payList) {
            maxCanRefund = maxCanRefund.add(NumberUtil.String2Dec(e.getAmount()));
        }
        for (RegistrationExpenseDetailPo e : refundedList) {
            maxCanRefund = maxCanRefund.subtract(NumberUtil.String2Dec(e.getAmount()));
        }
        //退费金额
        BigDecimal applyRefund = NumberUtil.String2Dec(payList.get(0).getPerAmount())
                .multiply(NumberUtil.String2Dec(payList.get(0).getDiscount())).multiply(BigDecimal.valueOf(itemList.size()));
        Assert.isTrue(applyRefund.compareTo(maxCanRefund) <= 0, "退费金额不可大于最大可退费金额，" + maxCanRefund);
        //生成退费记录
        RegistrationExpenseDetailFlow flow = new RegistrationExpenseDetailFlow();
        flow.setRegistrationId(ref.getRegistrationId());
        flow.setRegisterExpenseDetailId(ref.getId());
        flow.setFeeType(ref.getFeeType());
        flow.setType(ExpenseDetailFlowTypeEnum.退费.getCode());
        flow.setPerAmount(payList.get(0).getPerAmount());
        flow.setCount(itemList.size());
        flow.setDiscount(payList.get(0).getDiscount());
        flow.setAmount(applyRefund.toPlainString());
        flow.setStatus(StatusEnum.VALID.getCode());
        if (Consts.FEE_TYPE_普通单节费用.equals(ref.getFeeType())) {
            flow.setRemark(ExpenseDetailFlowTypeEnum.退费.getDescription() + "--" +
                    JSON.toJSONString(itemList.stream().map(e -> e.getRegisterSummaryId()).collect(Collectors.toList())));
        } else {
            flow.setRemark(ExpenseDetailFlowTypeEnum.退费.getDescription());
        }
        flow.setCreator(logInUser);
        flow.setCreateTime(new Date());
        flow.setOperator(logInUser);
        flow.setOperateTime(new Date());
        flow.setPayment(dto.getPayment());
        registrationExpenseDetailService.batchInsertFlow(Lists.newArrayList(flow));

        //如果所有的钱都退了那么就改变退费状态
        if (maxCanRefund.compareTo(applyRefund) == 0) {
            //修改支付状态
            ref.setFeeStatus(Enums.FeeStatus.已退费.getCode());
        }
        ref.setOperateTime(new Date());
        ref.setOperator(logInUser);
        ref.setAmount(NumberUtil.String2Dec(ref.getAmount()).subtract(applyRefund).toPlainString());
        ref.setCount(ref.getCount() - itemList.size());
        registrationExpenseDetailService.updateByPrimaryKeySelective(ref);
        return true;
    }

}
