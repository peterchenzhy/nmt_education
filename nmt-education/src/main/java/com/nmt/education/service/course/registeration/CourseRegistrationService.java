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
import com.nmt.education.service.authorization.AuthorizationCheckDto;
import com.nmt.education.service.authorization.AuthorizationDto;
import com.nmt.education.service.authorization.AuthorizationService;
import com.nmt.education.service.authorization.campus.CampusAuthorizationService;
import com.nmt.education.service.course.CourseService;
import com.nmt.education.service.course.registeration.summary.RegisterationSummaryService;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import com.nmt.education.service.student.StudentService;
import com.nmt.education.service.student.account.StudentAccountService;
import com.nmt.education.service.sysconfig.SysConfigService;
import lombok.SneakyThrows;
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
import java.util.function.Function;
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
    @Autowired
    private StudentAccountService studentAccountService;
    @Autowired
    @Lazy
    private SysConfigService sysConfigService;
    @Autowired
    private AuthorizationService authorizationService;


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
    public void startRegisterTransaction(CourseRegisterReqDto dto, int operator) {
        //生成报名记录
        CourseRegistrationPo courseRegistrationPo;
        if (Enums.EditFlag.新增.getCode().equals(dto.getEditFlag())) {
            Assert.isTrue(dto.getRegisterExpenseDetail().stream().anyMatch(e -> Consts.FEE_TYPE_普通单节费用.equals(e.getFeeType())), "报名必须含有单节收费项目");
            courseRegistrationPo = generateCourseRegistrationPo(dto, operator);
            this.insertSelective(courseRegistrationPo);
        } else {
            //编辑时，总课时仅可增加课时，不能减少
            courseRegistrationPo = selectByPrimaryKey(dto.getId());
            Assert.isTrue(Enums.RegistrationStatus.正常.getCode().equals(courseRegistrationPo.getRegistrationStatus()), "已经退费，请重新报名");
            courseRegistrationPo.setRemark(dto.getRemark());
            courseRegistrationPo.setOperator(operator);
            courseRegistrationPo.setOperateTime(new Date());
            BigDecimal prepTotal = new BigDecimal(courseRegistrationPo.getTotalAmount());
            BigDecimal prepBalance = new BigDecimal(courseRegistrationPo.getBalanceAmount());
            //计算增加金额
            BigDecimal newTotal = BigDecimal.ZERO;
            int addTimes = 0;
            for (RegisterExpenseDetailReqDto e : dto.getRegisterExpenseDetail()) {
                newTotal = newTotal.add(new BigDecimal(e.getAmount()));
                if (Consts.FEE_TYPE_普通单节费用.equals(e.getFeeType())) {
                    addTimes = e.getCount();
                }
            }
            Assert.isTrue(newTotal.compareTo(prepTotal) >= 0, "编辑金额不能小于原订单总额");
            Assert.isTrue(addTimes >= courseRegistrationPo.getTimes().intValue(), "报名课时只能比原来多");
            courseRegistrationPo.setTotalAmount(calculateTotalAmount(dto.getRegisterExpenseDetail()));
            courseRegistrationPo.setBalanceAmount(prepBalance.add(newTotal).subtract(prepTotal).toPlainString());
            courseRegistrationPo.setTimes(addTimes);
            courseRegistrationPo.setRegistrationStatus(Enums.RegistrationStatus.正常.getCode());
            this.updateByPrimaryKeySelective(courseRegistrationPo);
        }
        Assert.isTrue(Objects.nonNull(courseRegistrationPo), "非新增报名时，报名信息不存在，学生：" + dto.getStudentId() +
                "课程：" + dto.getCourseId());

        //缴费记录明细
        generateRegisterExpenseDetail(dto.getRegisterExpenseDetail(), operator, courseRegistrationPo, dto.isUseAccount(), dto.getBalanceAmount());

        //汇总课表
        registerationSummaryService.batchInsert(generateRegisterationSummary(dto, operator, courseRegistrationPo));
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
//        campusAuthorizationService.getCampusAuthorization(loginUserId, coursePo.getCampus());

        AuthorizationCheckDto reqDto = new AuthorizationCheckDto();
        reqDto.setUserId(loginUserId);
        reqDto.setCampus(coursePo.getCampus());
        reqDto.setGrade(coursePo.getGrade());
        authorizationService.getAuthorization(reqDto);

        Assert.notNull(coursePo, "课程信息不存在！id:" + dto.getCourseId());
        Assert.notEmpty(dto.getCourseScheduleIds(), "报名课时必填！id:" + dto.getCourseId());
        if (Enums.EditFlag.新增.getCode().equals(dto.getEditFlag())) {
            List<CourseRegistrationListVo> courseRegistrationListVos = queryByCourseStudent(dto.getCourseId(), dto.getStudentId());
            CourseRegistrationListVo vo =
                    courseRegistrationListVos.stream().filter(v -> Enums.RegistrationStatus.正常.getCode().equals(v.getRegistrationStatus()))
                            .findAny().orElse(null);
            if (Objects.nonNull(vo)) {
                Assert.isTrue(Enums.RegistrationStatus.已退费.getCode().equals(vo.getRegistrationStatus()),
                        "报名记录已经存在，不能重复报名！报名编号：" + vo.getRegistrationNumber());
            }
        }
        Assert.isTrue(!Enums.CourseStatus.已结课.getCode().equals(coursePo.getCourseStatus()) &&
                !Enums.CourseStatus.已取消.getCode().equals(coursePo.getCourseStatus()), "课程已经结课或者取消!");
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
    public List<CourseRegistrationListVo> queryByCourseStudent(Long courseId, Long studentId) {
        return this.courseRegistrationPoMapper.queryByCourseStudent(courseId, studentId);
    }


    /**
     * 缴费明细
     *
     * @param expenseDetailList    费用列表
     * @param updator              操作人
     * @param courseRegistrationPo 报名记录
     * @param studentAmount        结余账户的钱
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/5 0:02
     */
    private void generateRegisterExpenseDetail(List<RegisterExpenseDetailReqDto> expenseDetailList, int updator,
                                               CourseRegistrationPo courseRegistrationPo, boolean useAccount, BigDecimal studentAmount) {
        Assert.isTrue(!CollectionUtils.isEmpty(expenseDetailList), "报名时不存在费用信息");
        List<RegistrationExpenseDetailFlowPo> flowList = new ArrayList<>(expenseDetailList.size());

        //取结余
        BigDecimal account = BigDecimal.ZERO;
        //取结余流水
        List<StudentAccountFlowPo> accountFlowList = new ArrayList();
        StudentAccountPo studentAccountPo = null;
        if (useAccount) {
            studentAccountPo = studentAccountService.querybyUserId(courseRegistrationPo.getStudentId());
            if (Objects.nonNull(studentAccountPo)) {
                Assert.isTrue(NumberUtil.String2Dec(studentAccountPo.getAmount()).compareTo(studentAmount) == 0, "结余已变动，请重新编辑");
                account = account.add(NumberUtil.String2Dec(studentAccountPo.getAmount()));
            }
        }
        //是否使用结余表中
        boolean useAccountFlg = account.compareTo(BigDecimal.ZERO) > 0 && useAccount;
        //处理修改记录
        Map<Long, RegisterExpenseDetailReqDto> reqDtoMap =
                expenseDetailList.stream().filter(e -> Enums.EditFlag.修改.getCode().equals(e.getEditFlag()))
                        .collect(Collectors.toMap(k -> k.getId(), v -> v));
        List<RegistrationExpenseDetailPo> updateList =
                registrationExpenseDetailService.selectByIds(new ArrayList<>(reqDtoMap.keySet()));
        for (RegistrationExpenseDetailPo expenseDetailPo : updateList) {
            RegisterExpenseDetailReqDto dto = reqDtoMap.get(expenseDetailPo.getId());
            //增量金额
            BigDecimal delta = NumberUtil.String2Dec(dto.getAmount()).subtract(NumberUtil.String2Dec(expenseDetailPo.getAmount()));
            if (BigDecimal.ZERO.compareTo(delta) >= 0) {
                log.warn("金额没有变化，不生成流水，old: [{}], dto:[{}]", expenseDetailPo, dto);
                continue;
            } else {
                //先生成流水
                RegistrationExpenseDetailFlowPo flow = generateFlow(updator, expenseDetailPo, ExpenseDetailFlowTypeEnum.编辑, dto, delta.toPlainString());
                flowList.add(flow);
                //消耗 结余 逻辑
                if (useAccountFlg && account.compareTo(BigDecimal.ZERO) > 0) {
                    account = studentAccountCost(updator, courseRegistrationPo, account, accountFlowList, studentAccountPo, delta, flow);
                }
                //再编辑金额
                expenseDetailPo.setFeeType(dto.getFeeType());
                expenseDetailPo.setFeeStatus(Enums.FeeStatus.已缴费.getCode());
                expenseDetailPo.setAmount(dto.getAmount());
                expenseDetailPo.setPerAmount(dto.getPerAmount());
                expenseDetailPo.setCount(dto.getCount());
                expenseDetailPo.setDiscount(dto.getDiscount());
                expenseDetailPo.setPayment(dto.getPayment());
                expenseDetailPo.setRemark(expenseDetailPo.getRemark() + Consts.分号 + Strings.nullToEmpty(dto.getRemark()));
                expenseDetailPo.setOperator(updator);
                expenseDetailPo.setOperateTime(new Date());
            }
        }

        //处理新增记录
        for (RegisterExpenseDetailReqDto e : expenseDetailList) {
            if (Enums.EditFlag.新增.getCode().equals(e.getEditFlag())) {

                RegistrationExpenseDetailPo expenseDetailPo = registrationExpenseDetailService.dto2po(updator, courseRegistrationPo, e);
                registrationExpenseDetailService.batchInsert(Lists.newArrayList(expenseDetailPo));
                RegistrationExpenseDetailFlowPo flowPo = generateFlow(updator, expenseDetailPo, ExpenseDetailFlowTypeEnum.新增记录);
                //消耗 结余 逻辑
                if (useAccountFlg && account.compareTo(BigDecimal.ZERO) > 0) {
                    account = studentAccountCost(updator, courseRegistrationPo, account, accountFlowList, studentAccountPo,
                            new BigDecimal(flowPo.getAmount()), flowPo);
                }
                flowList.add(flowPo);
            }
        }

        //数据入库
        registrationExpenseDetailService.updateBatch(updateList);
        registrationExpenseDetailService.batchInsertFlow(flowList);
        //更新结余
        if (Objects.nonNull(studentAccountPo) && !CollectionUtils.isEmpty(accountFlowList)) {
            studentAccountPo.setOperateTime(new Date());
            studentAccountPo.setOperator(updator);
            studentAccountPo.setAmount(account.toPlainString());
            if (studentAccountService.updateByVersion(studentAccountPo) <= 0) {
                throw new RuntimeException("更新结余账户异常！");
            }
            studentAccountService.addFlow(accountFlowList);
        }

    }

    /**
     * 学生结余账户 逻辑
     *
     * @param updator              操作人
     * @param courseRegistrationPo 报名po
     * @param account              账户金额
     * @param accountFlowList
     * @param studentAccountPo
     * @param delta                消耗金额
     * @param flow
     * @return java.math.BigDecimal  结余账户 剩下的钱
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/9/6 16:14
     */
    private BigDecimal studentAccountCost(int updator, CourseRegistrationPo courseRegistrationPo, BigDecimal account, List<StudentAccountFlowPo> accountFlowList,
                                          StudentAccountPo studentAccountPo, BigDecimal delta, RegistrationExpenseDetailFlowPo flow) {
        //计算结余抵扣金额
        BigDecimal cost = calculateCost(account, delta);
        BigDecimal lastAmount = account;
        CoursePo coursePo = courseService.selectByPrimaryKey(courseRegistrationPo.getCourseId());
        flow.setRemark(String.format(Consts.结余消耗模板, coursePo.getName(), sysConfigService.queryByTypeValue(Consts.FEE_TYPE_费用类型,
                flow.getFeeType()).getDescription(),
                cost.toPlainString()));
        //设置结余消耗金额
        flow.setAccountAmount(cost.toPlainString());
        //更新账户余额
        account = account.subtract(cost);
        //生成结余流水
        accountFlowList.add(studentAccountService.generateFlow(updator, studentAccountPo.getId(), account.toPlainString(),
                ExpenseDetailFlowTypeEnum.消耗,
                courseRegistrationPo.getId(), lastAmount.toPlainString(), flow.getRemark()));
        return account;
    }

    /**
     * 计算消耗
     *
     * @param account 账户余额
     * @param delta   消耗金额
     * @return
     */
    private BigDecimal calculateCost(BigDecimal account, BigDecimal delta) {
        BigDecimal temp = account.subtract(delta);
        if (temp.compareTo(BigDecimal.ZERO) >= 0) {
            return delta;
        } else {
            return account;
        }
    }

    /**
     * 生成流水 用于编辑 新增课时
     *
     * @param updator 操作人
     * @param old     老数据
     * @param type    类型
     * @param dto     请求数据
     * @return com.nmt.education.pojo.po.RegistrationExpenseDetailFlow
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/5 0:14
     */
    private RegistrationExpenseDetailFlowPo generateFlow(int updator, RegistrationExpenseDetailPo old, ExpenseDetailFlowTypeEnum type,
                                                         RegisterExpenseDetailReqDto dto, String delta) {

        RegistrationExpenseDetailFlowPo flow = new RegistrationExpenseDetailFlowPo();
        flow.setRegistrationId(old.getRegistrationId());
        flow.setRegisterExpenseDetailId(old.getId());
        flow.setFeeType(old.getFeeType());
        flow.setType(type.getCode());
        flow.setAmount(delta);
        flow.setStatus(StatusEnum.VALID.getCode());
        flow.setRemark(type.getDescription());
        flow.setCreator(updator);
        flow.setCreateTime(new Date());
        flow.setOperator(updator);
        flow.setOperateTime(new Date());
        flow.setPerAmount(dto.getPerAmount());
        flow.setCount(dto.getCount());
        flow.setDiscount(dto.getDiscount());
        flow.setPayment(dto.getPayment());
        flow.setAccountAmount(Consts.ZERO);
        return flow;
    }

    /**
     * 生成流水 用于新增
     *
     * @param updator
     * @param e
     * @param type
     * @return com.nmt.education.pojo.po.RegistrationExpenseDetailFlow
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/5 0:11
     */
    private RegistrationExpenseDetailFlowPo generateFlow(int updator, RegistrationExpenseDetailPo e, ExpenseDetailFlowTypeEnum type) {
        RegistrationExpenseDetailFlowPo flow = new RegistrationExpenseDetailFlowPo();
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
        flow.setAccountAmount(Consts.ZERO);
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
        List<Integer> campusList = new ArrayList<>();
//        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser);
        AuthorizationDto authorization = authorizationService.getAuthorization(logInUser);
        if (dto.getCampus() != null) {
            Assert.isTrue(authorization.getCampusList().contains(dto.getCampus()), "该校区没有权限");
            campusList.add(dto.getCampus());
        } else {
            campusList.addAll(authorization.getCampusList());
        }

        List<Integer> gradeList = new ArrayList<>();
        if (dto.getGrade() != null) {
            Assert.isTrue(authorization.getGradeList().contains(dto.getGrade()), "该年级没有权限");
            gradeList.add(dto.getGrade());
        } else {
            gradeList.addAll(authorization.getGradeList());
        }


        dto.setSignInDateStart(Objects.nonNull(dto.getSignInDateStart()) ? DateUtil.parseOpenDate(dto.getSignInDateStart()) : null);
        dto.setSignInDateEnd(Objects.nonNull(dto.getSignInDateEnd()) ? DateUtil.parseCloseDate(dto.getSignInDateEnd()) : null);
        return PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.courseRegistrationPoMapper.queryByDto(dto,
                campusList,gradeList));
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
//        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser, dto.getCampus());

        AuthorizationCheckDto req = new AuthorizationCheckDto() ;
        req.setUserId(logInUser);
        req.setCampus(dto.getCampus());
        req.setGrade(dto.getGrade());
        AuthorizationDto authorization = authorizationService.getAuthorization(req);

        Assert.isTrue(!CollectionUtils.isEmpty(authorization.getCampusList()), "没有任何校区权限进行搜索");
        Assert.isTrue(!CollectionUtils.isEmpty(authorization.getGradeList()), "没有任何年级权限进行搜索");
        PageInfo<RegisterSummaryVo> pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> queryBySearchDto(dto
                , authorization.getCampusList(),authorization.getGradeList()));
        return pageInfo;
    }

    public RegisterSummaryTotalVo registerSummaryTotal(RegisterSummarySearchDto dto, Integer logInUser) {
//        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser, dto.getCampus());

        AuthorizationCheckDto reqDto = new AuthorizationCheckDto();
        reqDto.setUserId(logInUser);
        reqDto.setCampus(dto.getCampus());
        reqDto.setGrade(dto.getGrade());
        AuthorizationDto authorization = authorizationService.getAuthorization(reqDto);

        List<Integer> campusList =authorization.getCampusList();
        List<Integer> gradeList =authorization.getGradeList() ;

        Assert.isTrue(!CollectionUtils.isEmpty(campusList), "没有任何校区权限进行搜索");
        Assert.isTrue(!CollectionUtils.isEmpty(gradeList), "没有任何年级权限进行搜索");
        RegisterSummaryTotalVo vo = new RegisterSummaryTotalVo();
        if (Objects.nonNull(dto.getEndDate())) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        if (Objects.nonNull(dto.getRegisterEndDate())) {
            dto.setRegisterEndDate(DateUtil.parseCloseDate(dto.getRegisterEndDate()));
        }
        if (Objects.nonNull(dto.getStartDate())) {
            dto.setStartDate(DateUtil.parseOpenDate(dto.getStartDate()));
        }
        vo.setTotalCount(queryCountBySearchDto(dto, campusList, null,gradeList));
        vo.setSignInCount(queryCountBySearchDto(dto, campusList, Enums.SignInType.已签到.getCode(),gradeList));

        long count = registerStudentSummaryTotal(dto.getStartDate(), dto.getEndDate(), dto.getYear(), dto.getSeason(), null, campusList,gradeList);
        vo.setRegisterStudentCount(count);

        vo.setRegisterCount(this.countRegistration(dto, campusList));

        vo.setUnSignInCount(vo.getTotalCount() - vo.getSignInCount());
        return vo;
    }


    /**
     * 科目统计
     *
     * @return
     */
    private long countRegistration(RegisterSummarySearchDto dto, List<Integer> campusList) {

        return this.registerationSummaryService.countRegistration(dto, campusList);

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
    public List<RegisterSummaryVo> queryBySearchDto(RegisterSummarySearchDto dto, List<Integer> campusList,List<Integer> gradeList) {

        return this.registerationSummaryService.queryBySearchDto(dto, campusList,gradeList);
    }

    private int queryCountBySearchDto(RegisterSummarySearchDto dto, List<Integer> campusList, Integer signInStatus,List<Integer> gradeList) {

        return this.registerationSummaryService.queryCountBySearchDto(dto, campusList, signInStatus,  gradeList);
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
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return courseRegistrationPoMapper.updateBatch(list);
    }


    public int updateBatchSelective(List<CourseRegistrationPo> list) {
        return courseRegistrationPoMapper.updateBatchSelective(list);
    }


    public int batchInsert(List<CourseRegistrationPo> list) {
        return courseRegistrationPoMapper.batchInsert(list);
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
    @SneakyThrows
    public CourseRegistrationVo registerDetail(Long id, Integer logInUser) {
        if (Objects.isNull(id)) {
            return null;
        }
        CourseRegistrationVo vo = this.courseRegistrationPoMapper.queryVoById(id);
        Assert.notNull(vo, "无法查询到报名记录，id:" + id);

        Thread flowThread =
                new Thread(() -> vo.setExpenseDetailFlowVoList(this.registrationExpenseDetailService.getExpenseDetailFlowVo(vo.getId())));
        flowThread.start();

        Thread flowThread2 =
                new Thread(() -> vo.setStudentAccountFlowPoList(this.studentAccountService.queryFlowByRegisterId(vo.getId())
                .stream().filter(e->NumberUtil.String2Dec(e.getAmount()).compareTo(NumberUtil.String2Dec(e.getBeforeAmount()))<0)
                .collect(Collectors.toList())));
        flowThread2.start();

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
        //等待异步线程完成
        flowThread.join();
        flowThread2.join();
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
    public List<SignRecordVo> registerStudent(Long courseId) {
        List<CourseRegistrationPo> courseRegistrationList = this.courseRegistrationPoMapper.queryByCourseId(courseId, Enums.RegistrationStatus.正常.getCode());
        if (CollectionUtils.isEmpty(courseRegistrationList)) {
            return Collections.emptyList();
        }

        Map<Long, StudentPo> studentMap =
                studentService.queryByIds(courseRegistrationList.stream().map(CourseRegistrationPo::getStudentId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(StudentPo::getId, v -> v));

        Map<Long, List<RegisterationSummaryPo>> summaryMap =
                registerationSummaryService.queryByRegisterIds(courseRegistrationList.stream().map(e -> e.getId()).collect(Collectors.toList()))
                        .stream().collect(Collectors.groupingBy(k -> k.getStudentId()));

        Map<Long, CourseSchedulePo> courseScheduleMap =
                courseScheduleService.queryByCourseId(courseId).stream().collect(Collectors.toMap(CourseSchedulePo::getId, Function.identity()));

        List<SignRecordVo> voList = new ArrayList<>(courseRegistrationList.size());
        courseRegistrationList.stream().forEach(e -> {
            SignRecordVo vo = new SignRecordVo();
            StudentPo studentPo = studentMap.get(e.getStudentId());
            if(Objects.nonNull(studentPo)){
                vo.setStudentId(studentPo.getId());
                vo.setName(studentPo.getName());
                vo.setCode(studentPo.getStudentCode());
                vo.setSchool(studentPo.getSchool());
                vo.setPhone(studentPo.getPhone());
                vo.setGrade(studentPo.getGrade());
                vo.setSex(studentPo.getSex());
            }
            vo.setCourseRegisterId(e.getId());
            //填充报名的课程
            vo.setSignInMap(summaryMap.get(vo.getStudentId()).stream().collect(Collectors.toMap(k -> k.getCourseScheduleId(),
                    v -> new SignRecordVo.SignInfo(v.getSignIn(), v.getSignInRemark(), v.getId()), (k1, k2) -> k2)));
            //填充未报名的课程
            final Map<Long, SignRecordVo.SignInfo> noSignMap =
                    courseScheduleMap.keySet().stream().filter(k -> Objects.isNull(vo.getSignInMap().get(k)))
                            .collect(Collectors.toMap(k1 -> k1, v -> new SignRecordVo.SignInfo(-1, "", -1L)));
            vo.getSignInMap().putAll(noSignMap);
            voList.add(vo);
        });
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
        CourseRegistrationPo courseRegistrationPo = courseRegistrationPoMapper.selectByPrimaryKey(dto.getRegisterId());
        Assert.isTrue(Objects.nonNull(courseRegistrationPo), "报名信息不存在，id：" + dto.getRegisterId());
        Assert.isTrue(!Enums.RegistrationStatus.已退费.getCode().equals(courseRegistrationPo.getRegistrationStatus()),
                "已退费的订单无法重复退费，id：" + dto.getRegisterId());
        CoursePo coursePo = courseService.selectByPrimaryKey(courseRegistrationPo.getCourseId());
        Assert.isTrue(!Enums.CourseStatus.已结课.getCode().equals(coursePo.getCourseStatus()) &&
                !Enums.CourseStatus.已取消.getCode().equals(coursePo.getCourseStatus()), "课程已经结课或者取消!");

        List<RefundItemReqDto> dtoList = dto.getItemList();
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }
        List<RegistrationExpenseDetailPo> expenseDetailList = registrationExpenseDetailService.queryRegisterId(dto.getRegisterId());
        //按照费用类型分组
        Map<Integer, List<RefundItemReqDto>> itemMap = dtoList.stream().collect(Collectors.groupingBy(e -> e.getFeeType()));
        //退费核心逻辑
        BigDecimal refundTotal  = refundByFeeType(dto, logInUser, expenseDetailList, itemMap, courseRegistrationPo);

        int refundTimes = 0;
        for (RefundItemReqDto refundItemReqDto : dto.getItemList()) {
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
        if (registrationExpenseDetailService.queryRegisterId(dto.getRegisterId())
                .stream().filter(e -> Enums.FeeDirection.支付.getCode().equals(e.getFeeDirection()))
                .allMatch(e -> Enums.FeeStatus.已退费.getCode().equals(e.getFeeStatus()))) {
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
    private BigDecimal refundByFeeType(RefundReqDto dto, Integer logInUser, List<RegistrationExpenseDetailPo> expenseDetailList, Map<Integer,
            List<RefundItemReqDto>> itemMap, CourseRegistrationPo courseRegistrationPo) {
        BigDecimal reFundTotal = BigDecimal.ZERO;
        //按费用类型进行退款
        for (Integer k : itemMap.keySet()) {
            if (k.equals(Consts.FEE_TYPE_普通单节费用)) {
                //查询消耗情况
                List<RegisterationSummaryPo> registrationSummaryPoList = registerationSummaryService.queryByRegisterId(dto.getRegisterId());
                RegisterationSummaryPo p = registrationSummaryPoList.stream()
                        .filter(e -> itemMap.get(k).contains(e.getId()) && !Enums.SignInType.CAN_REFUND.contains(e.getSignIn())).findAny().orElse(null);
                if (Objects.nonNull(p)) {
                    throw new RuntimeException("有记录已经被退费或者被消费了，RegisterationSummaryPoId:" + p.getId());
                }

            }
            //退费逻辑
            if (!CollectionUtils.isEmpty(itemMap.get(k))) {
                reFundTotal = reFundTotal.add(processRefund(dto, logInUser, itemMap.get(k), expenseDetailList, k));
                if (k.equals(Consts.FEE_TYPE_普通单节费用)) {
                    //更新报名课表
                    registerationSummaryService.updateSignIn(itemMap.get(k).stream().map(e -> e.getRegisterSummaryId()).collect(Collectors.toList()),
                            logInUser, Enums.SignInType.已退费, Enums.SignInType.已退费.getDesc());
                }
            } else {
                log.info("费用类型：{},没有项目明细，退费逻辑终止，dto：{}", k, dto);
            }
        }

        Assert.isTrue(reFundTotal.compareTo(new BigDecimal(courseRegistrationPo.getBalanceAmount())) <= 0, "退费金额大于可用余额");

        //如果退费直接进结余
        if (dto.getToAccount() && reFundTotal.compareTo(BigDecimal.ZERO) > 0) {
            CoursePo coursePo = courseService.selectByPrimaryKey(courseRegistrationPo.getCourseId());
            studentAccountService.addAmount(logInUser, courseRegistrationPo.getStudentId(),
                    reFundTotal, courseRegistrationPo.getCourseRegistrationId(), String.format(Consts.退费进学生账户REMARK, coursePo.getName(),
                            reFundTotal.toPlainString()));

        }
        return reFundTotal ;
    }

    /**
     * 处理退费
     *
     * @param dto
     * @param logInUser
     * @param itemList
     * @param expenseDetailList
     * @param feeType
     * @return 退费金额
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/6/6 14:38
     */
    private BigDecimal processRefund(RefundReqDto dto, Integer logInUser, List<RefundItemReqDto> itemList
            , List<RegistrationExpenseDetailPo> expenseDetailList, Integer feeType) {
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
        BigDecimal applyRefund ;
        if (Consts.FEE_TYPE_普通单节费用.equals(ref.getFeeType())) {
            applyRefund = NumberUtil.String2Dec(payList.get(0).getPerAmount())
                    .multiply(NumberUtil.String2Dec(payList.get(0).getDiscount())).multiply(BigDecimal.valueOf(itemList.size()));
        }else{
            applyRefund = NumberUtil.String2Dec(payList.get(0).getAmount());
        }

        Assert.isTrue(applyRefund.compareTo(maxCanRefund) <= 0, "退费金额不可大于最大可退费金额，" + maxCanRefund);
        //生成退费记录
        RegistrationExpenseDetailFlowPo flow = new RegistrationExpenseDetailFlowPo();
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
        flow.setAccountAmount(Consts.ZERO);
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
        return applyRefund;
    }

    public List<CourseRegisterCount> countStudentByCourse(List<Long> courseIds) {
        if (CollectionUtils.isEmpty(courseIds)) {
            return Collections.emptyList();
        }
        return this.courseRegistrationPoMapper.countStudentByCourse(courseIds);

    }

    //统计报名的学生数量
    public long registerStudentSummaryTotal(Date startDate, Date endDate, Integer year, Integer season, Integer userCode, List<Integer> campusList,
                                            List<Integer> gradeList) {
        return this.courseRegistrationPoMapper.registerStudentSummaryTotal(startDate, endDate, year, season, userCode, campusList,  gradeList);
    }

    /**
     * 根据课程id 获取查询报名记录
     *
     * @param courseId
     * @return java.util.List<com.nmt.education.pojo.po.CourseRegistrationPo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2021/1/28 22:19
     */
    public List<CourseRegistrationPo> queryByCourseId(long courseId) {
        return this.courseRegistrationPoMapper.queryByCourseId(courseId, null);
    }
}
