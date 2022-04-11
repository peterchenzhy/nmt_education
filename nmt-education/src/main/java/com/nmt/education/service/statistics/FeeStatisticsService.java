package com.nmt.education.service.statistics;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.nmt.education.commmons.*;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.dto.req.FeeStatisticsReqDto;
import com.nmt.education.pojo.dto.req.TeacherScheduleReqDto;
import com.nmt.education.pojo.po.StudentAccountFlowPo;
import com.nmt.education.pojo.vo.FeeStatisticsVo;
import com.nmt.education.pojo.vo.FeeSummaryVo;
import com.nmt.education.service.authorization.AuthorizationCheckDto;
import com.nmt.education.service.authorization.AuthorizationDto;
import com.nmt.education.service.authorization.AuthorizationService;
import com.nmt.education.service.course.registeration.CourseRegistrationService;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailFlowDto;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailService;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import com.nmt.education.service.student.account.StudentAccountService;
import com.nmt.education.service.sysconfig.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FeeStatisticsService {

    @Autowired
    private RegistrationExpenseDetailService registrationExpenseDetailService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private CourseScheduleService courseScheduleService;
    @Autowired
    private CourseRegistrationService courseRegistrationService;
    @Autowired
    private StudentAccountService studentAccountService;

    /**
     * 分页查询接口
     *
     * @param dto
     * @param logInUser
     * @return com.github.pagehelper.PageInfo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/22 22:38
     */
    public PageInfo page(FeeStatisticsReqDto dto, Integer logInUser) {
//        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser, dto.getCampus());
        AuthorizationCheckDto reqDto = new AuthorizationCheckDto();
        reqDto.setUserId(logInUser);
        reqDto.setCampus(dto.getCampus());
        AuthorizationDto authorization = authorizationService.getAuthorization(reqDto);

        if (dto.getEndDate() != null) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        PageInfo<FeeStatisticsVo> pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() ->
                registrationExpenseDetailService.feeStatistics(dto.getStartDate(), dto.getEndDate(), dto.getYear(), dto.getSeason(),
                        authorization.getCampusList(),
                        ExpenseDetailFlowTypeEnum.feeStatistics2FlowType(dto.getFeeFlowType()), dto.getUserCode()));
        //退费入结余的数据
        Map<Long, BigDecimal> refund2AccountMap = getRefund2AccountMap(pageInfo.getList());

        pageInfo.getList().stream().forEach(e -> {
            e.setFeeFlowTypeStr(ExpenseDetailFlowTypeEnum.codeOf(e.getFeeFlowType()).getDisplay());
            e.setPaymentStr(Enums.PaymentType.codeOf(e.getPayment()).getDesc());
            setRefundAccountAmount(refund2AccountMap, e);
            e.setActuallyAmount();
        });
        return pageInfo;
    }

    /**
     * 退费进结余的数据处理
     * @param refund2AccountMap
     * @param e
     */
    private void setRefundAccountAmount(Map<Long, BigDecimal> refund2AccountMap, FeeStatisticsVo e) {
        if (Objects.equals(e.getFeeFlowType(), ExpenseDetailFlowTypeEnum.退费.getCode())) {
            BigDecimal amount = refund2AccountMap.get(e.getRegisterId());
            if (Objects.nonNull(amount) && amount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal remainAccount = amount.subtract(NumberUtil.String2Dec(e.getAmount()));
                if (remainAccount.compareTo(BigDecimal.ZERO) >= 0) {
                    e.setAccountAmount(e.getAmount());
                    //更新map中的结余数据
                    refund2AccountMap.put(e.getRegisterId(), remainAccount);
                }else{
                    e.setAccountAmount(amount.toPlainString());
                    e.setAmount(NumberUtil.String2Dec(e.getAccountAmount()).subtract(amount).toPlainString());
                    refund2AccountMap.put(e.getRegisterId(), BigDecimal.ZERO);
                }
            }
        }
    }

    /**
     * 获取退费进结余 数据
     * @param list
     * @return
     */
    private Map<Long, BigDecimal> getRefund2AccountMap(List<FeeStatisticsVo> list) {
        Map<Long, BigDecimal> refund2AccountMap =
                studentAccountService.queryFlowByRegisterIds(list.stream().filter(e -> Objects.equals(e.getFeeFlowType(),
                        ExpenseDetailFlowTypeEnum.退费.getCode()))
                        .map(FeeStatisticsVo::getRegisterId).collect(Collectors.toList()))
                        .stream()
                        .filter(e -> e.getSource() == AccountFlowSourceEnum.退费.getCode())
//                        .filter(e -> NumberUtil.String2Dec(e.getAmount()).compareTo(NumberUtil.String2Dec(e.getBeforeAmount())) > 0)
                        .collect(Collectors.groupingBy(e -> e.getRefId(),
                                Collectors.reducing(BigDecimal.ZERO,
                                        e -> NumberUtil.String2Dec(e.getAmount()).subtract(NumberUtil.String2Dec(e.getBeforeAmount())),
                                        BigDecimal::add)));
        return refund2AccountMap;
    }


    /**
     * 分页导出
     *
     * @param dto
     * @param logInUser
     * @return java.util.List<com.nmt.education.pojo.vo.FeeStatisticsVo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/25 15:41
     */
    public List<FeeStatisticsVo> exportList(FeeStatisticsReqDto dto, Integer logInUser) {
        //获取权限范围
//        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser, dto.getCampus());
        AuthorizationCheckDto reqDto = new AuthorizationCheckDto();
        reqDto.setUserId(logInUser);
        reqDto.setCampus(dto.getCampus());
        AuthorizationDto authorization = authorizationService.getAuthorization(reqDto);

        if (dto.getEndDate() != null) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        List<FeeStatisticsVo> resultList = new ArrayList<>(Consts.BATCH_100);
        dto.setPageNo(1);
        dto.setPageSize(Consts.BATCH_100);
        List<FeeStatisticsVo> dataList;
        do {
            dataList = getExportData(dto, authorization.getCampusList());
            int pageNo = dto.getPageNo() + 1;
            dto.setPageNo(pageNo);
            resultList.addAll(dataList);
        } while (!CollectionUtils.isEmpty(dataList));

        return resultList;

    }

    private List<FeeStatisticsVo> getExportData(FeeStatisticsReqDto dto, List<Integer> campusList) {
        PageInfo<FeeStatisticsVo> pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize(), false).doSelectPageInfo(() ->
                registrationExpenseDetailService.feeStatistics(dto.getStartDate(), dto.getEndDate(), dto.getYear(), dto.getSeason(), campusList,
                        ExpenseDetailFlowTypeEnum.feeStatistics2FlowType(dto.getFeeFlowType()), dto.getUserCode()));

        Map<Long, BigDecimal> refund2AccountMap = this.getRefund2AccountMap(pageInfo.getList());

        pageInfo.getList().stream().forEach(e -> {
                    e.setFeeFlowTypeStr(ExpenseDetailFlowTypeEnum.codeOf(e.getFeeFlowType()).getDisplay());
                    e.setCampusStr(sysConfigService.queryByTypeValue(SysConfigEnum.校区.getCode(), e.getCampus()).getDescription());
                    e.setPaymentStr(Enums.PaymentType.codeOf(e.getPayment()).getDesc());
                    e.setGradeStr(sysConfigService.queryByTypeValue(Consts.CONFIG_TYPE_年级,e.getGrade()).getDescription());
                    e.setSubjectStr(sysConfigService.queryByTypeValue(Consts.CONFIG_TYPE_科目,e.getSubject()).getDescription());
                    e.setFeeTimeDate(DateUtil.formatDate(e.getFeeTime()));
                    setRefundAccountAmount(refund2AccountMap,e);
            e.setActuallyAmount();
                }
        );
        return pageInfo.getList();
    }

    //一段时间内 收费 退费 教师课时费统计
    public FeeSummaryVo summary(FeeStatisticsReqDto dto, Integer logInUser, boolean isManager) {
        Date startDate = DateUtil.parseOpenDate(dto.getStartDate());
        Date endDate = DateUtil.parseCloseDate(dto.getEndDate());
        FeeSummaryVo vo = new FeeSummaryVo();
        AuthorizationCheckDto reqDto = new AuthorizationCheckDto();
        reqDto.setUserId(logInUser);
        reqDto.setCampus(dto.getCampus());
        AuthorizationDto authorization = authorizationService.getAuthorization(reqDto);
        List<Integer> campusList = authorization.getCampusList();

        //总费用
        List<RegistrationExpenseDetailFlowDto> payList = registrationExpenseDetailService.flowSummary(startDate, endDate,
                dto.getYear(), dto.getSeason(), campusList, dto.getUserCode(),
                Lists.newArrayList(ExpenseDetailFlowTypeEnum.新增记录.getCode(), ExpenseDetailFlowTypeEnum.编辑.getCode()));
        vo.setPay(NumberUtil.addStringList(
                payList.stream().map(RegistrationExpenseDetailFlowDto::getAmount).collect(Collectors.toList())).stripTrailingZeros().toPlainString());

        //总抵扣
        List<String> amountSummary = registrationExpenseDetailService.flowAmountSummary(startDate, endDate, dto.getYear(), dto.getSeason(), campusList,
                dto.getUserCode(),
                Lists.newArrayList(ExpenseDetailFlowTypeEnum.新增记录.getCode(), ExpenseDetailFlowTypeEnum.编辑.getCode()));
        vo.setAmountSummary(NumberUtil.addStringList(amountSummary).stripTrailingZeros().toPlainString());

        //总退费
        List<RegistrationExpenseDetailFlowDto> refundList = registrationExpenseDetailService.flowSummary(startDate, endDate, dto.getYear(), dto.getSeason(), campusList,
                dto.getUserCode(), Lists.newArrayList(ExpenseDetailFlowTypeEnum.退费.getCode()));
        vo.setRefund(NumberUtil.addStringList(
                refundList.stream().map(RegistrationExpenseDetailFlowDto::getAmount).collect(Collectors.toList())
        ).stripTrailingZeros().toPlainString());

        //退费进结余部分
        List<StudentAccountFlowPo> refund2AccountList =
                studentAccountService.queryFlowByRegisterIds(refundList.stream().map(RegistrationExpenseDetailFlowDto::getRegistrationId).collect(Collectors.toList()))
                .stream().filter(e -> NumberUtil.String2Dec(e.getAmount()).compareTo(NumberUtil.String2Dec(e.getBeforeAmount())) > 0).collect(Collectors.toList());
        vo.setRefund2Account(
        refund2AccountList.stream()
                .filter(e->e.getSource()==AccountFlowSourceEnum.退费.getCode())
                .map(e->NumberUtil.String2Dec(e.getAmount()).subtract(NumberUtil.String2Dec(e.getBeforeAmount())))
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO).stripTrailingZeros().toPlainString()
        );
        //报名人数
        long count = courseRegistrationService.registerStudentSummaryTotal(startDate, endDate, dto.getYear(), dto.getSeason(), dto.getUserCode(),
                campusList,
                authorization.getGradeList());
        vo.setRegisterStudentCount(count);

        if (isManager) {
            TeacherScheduleReqDto teacherScheduleReqDto = new TeacherScheduleReqDto();
            teacherScheduleReqDto.setStartDate(startDate);
            teacherScheduleReqDto.setEndDate(endDate);
            teacherScheduleReqDto.setYear(dto.getYear());
            teacherScheduleReqDto.setSeason(dto.getSeason());
            teacherScheduleReqDto.setCampus(dto.getCampus());
            List<String> teacherPay = courseScheduleService.getTeacherPay(teacherScheduleReqDto, logInUser);
            vo.setTeacherPay(NumberUtil.addStringList(teacherPay).stripTrailingZeros().toPlainString());
        }
        vo.setStartDate(DateUtil.formatDate(startDate));
        vo.setEndDate(DateUtil.formatDate(endDate));
        vo.setActuallyHandIn();
        return vo;
    }
}
