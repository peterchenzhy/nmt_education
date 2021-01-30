package com.nmt.education.service.export;

import com.google.common.collect.Lists;
import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.ExpenseDetailFlowTypeEnum;
import com.nmt.education.commmons.NumberUtil;
import com.nmt.education.pojo.dto.req.CourseSearchDto;
import com.nmt.education.pojo.dto.req.SummaryExportReqDto;
import com.nmt.education.pojo.po.*;
import com.nmt.education.pojo.vo.SummaryExportDto;
import com.nmt.education.service.campus.authorization.CampusAuthorizationService;
import com.nmt.education.service.course.CourseService;
import com.nmt.education.service.course.expense.CourseExpenseService;
import com.nmt.education.service.course.registeration.CourseRegistrationService;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailService;
import com.nmt.education.service.course.registeration.summary.RegisterationSummaryService;
import com.nmt.education.service.student.StudentService;
import com.nmt.education.service.sysconfig.SysConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SummaryExportService extends AbstractExportService<SummaryExportReqDto, SummaryExportDto> {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CampusAuthorizationService campusAuthorizationService;
    @Autowired
    private CourseRegistrationService courseRegistrationService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private RegistrationExpenseDetailService expenseDetailService;
    @Autowired
    private RegisterationSummaryService registerationSummaryService;
    @Autowired
    private CourseExpenseService courseExpenseService;
    @Autowired
    private StudentService studentService;


    @Override
    public List<SummaryExportDto> getDataList(SummaryExportReqDto dto, Integer logInUser) {

        //先选课程
        List<CoursePo> coursePos = getCourseList(dto, logInUser);
        if (CollectionUtils.isEmpty(coursePos)) {
            return Collections.emptyList();
        }

        List<SummaryExportDto> summaryExportDtoList = new ArrayList<>(coursePos.size() * Consts.BATCH_10);
        //对每个课程进行遍历
        coursePos.forEach(coursePo -> {
            //初始化dto
            SummaryExportDto baseSummaryExportDto = initByCoursePo(coursePo);

            //先取课程下的报名记录 k->学生id
            Map<Long, List<CourseRegistrationPo>> userRegistrationMap = courseRegistrationService.queryByCourseId(coursePo.getId())
                    .stream().collect(Collectors.groupingBy(CourseRegistrationPo::getStudentId));

            //报名ids
            List<Long> registerIds = userRegistrationMap.values().stream()
                    .flatMap(l -> l.stream().map(CourseRegistrationPo::getId)).collect(Collectors.toList());

            //学生信息
            Map<Long, String> studentMap = studentService.queryByIds(
                    userRegistrationMap.values().stream()
                            .flatMap(l -> l.stream().map(CourseRegistrationPo::getStudentId)).collect(Collectors.toList())
            ).stream().collect(Collectors.toMap(StudentPo::getId, StudentPo::getName));

            //获取付费记录 k->报名id
            Map<Long, List<RegistrationExpenseDetailPo>> registerExpenseMap =
                    expenseDetailService.queryRegisterIds(registerIds)
                            .stream().collect(Collectors.groupingBy(RegistrationExpenseDetailPo::getRegistrationId));
            //退费流水
            Map<Long, List<RegistrationExpenseDetailFlowPo>> registerExpenseRefundFlowMap = expenseDetailService.getExpenseDetailFlowList(registerIds,
                    Lists.newArrayList(ExpenseDetailFlowTypeEnum.退费.getCode(), ExpenseDetailFlowTypeEnum.编辑.getCode(), ExpenseDetailFlowTypeEnum.新增记录.getCode()))
                    .stream().collect(Collectors.groupingBy(RegistrationExpenseDetailFlowPo::getRegistrationId));

            //报名汇总表 k->报名id
            Map<Long, List<RegisterationSummaryPo>> registerSummaryMap = registerationSummaryService.queryByRegisterIds(registerIds)
                    .stream().collect(Collectors.groupingBy(RegisterationSummaryPo::getCourseRegistrationId));

            //对于每个学生有多条报名记录的暂不聚合出两条数据
            userRegistrationMap.forEach((k, v) -> {
                v.stream().forEach(registration -> {
                    SummaryExportDto personSummaryExportDto = new SummaryExportDto();
                    BeanUtils.copyProperties(baseSummaryExportDto, personSummaryExportDto);

                    //处理报名缴费记录
                    processRegisterExpenseDetail(personSummaryExportDto, registerExpenseMap.get(registration.getId()),
                            registerExpenseRefundFlowMap.get(registration.getId()));
                    //处理考勤
                    processAttendance(registerSummaryMap.get(registration.getId()), personSummaryExportDto);

                    //学生姓名
                    personSummaryExportDto.setStudentName(studentMap.get(registration.getStudentId()));
                    personSummaryExportDto.setRegisterRemark(registration.getRemark());
                    // add dto list
                    summaryExportDtoList.add(personSummaryExportDto.calcFields());
                });
            });
        });

        return summaryExportDtoList;
    }

    private void processAttendance(List<RegisterationSummaryPo> registerationSummaryPoList1, SummaryExportDto personSummaryExportDto) {
        List<RegisterationSummaryPo> registerationSummaryPoList = registerationSummaryPoList1;
        long signInCount = registerationSummaryPoList.stream().filter(e -> Enums.SignInType.已签到.getCode().equals(e.getSignIn())).count();
        long refundCount = registerationSummaryPoList.stream().filter(e -> Enums.SignInType.已退费.getCode().equals(e.getSignIn())).count();
        long absence = registerationSummaryPoList.size() - signInCount - refundCount;
        personSummaryExportDto.setActuallyAttendance(signInCount);
        personSummaryExportDto.setActuallyAbsence(absence);
    }

    //处理RegisterExpenseDetail
    private void processRegisterExpenseDetail(SummaryExportDto summaryExportDto, List<RegistrationExpenseDetailPo> expenseDetailPoList,
                                              List<RegistrationExpenseDetailFlowPo> registrationExpenseDetailFlowPos) {
        expenseDetailPoList.stream().forEach(expenseDetail -> {
            if (Consts.FEE_TYPE_普通单节费用.equals(expenseDetail.getFeeType())) {
                summaryExportDto.setDiscount(expenseDetail.getDiscount());
                summaryExportDto.setActuallyApplyAttendance(expenseDetail.getCount());
                summaryExportDto.setActuallyPerPrice(expenseDetail.getPerAmount());
            } else {
                //实收材料费
                summaryExportDto.setActuallyBookFee(expenseDetail.getAmount());

            }
        });

        //退费合计
        BigDecimal refundAmount =
                registrationExpenseDetailFlowPos.stream().filter(e -> ExpenseDetailFlowTypeEnum.退费.getCode() == e.getType())
                        .map(e -> NumberUtil.String2Dec(e.getAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
        summaryExportDto.setRefund(refundAmount.toPlainString());
        //结余合计
        BigDecimal accountAmount =
                registrationExpenseDetailFlowPos.stream()
                        .filter(e -> ExpenseDetailFlowTypeEnum.编辑.getCode() == e.getType() || ExpenseDetailFlowTypeEnum.新增记录.getCode() == e.getType())
                        .map(e -> NumberUtil.String2Dec(e.getAccountAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
        summaryExportDto.setSurplusDeduction(accountAmount.toPlainString());
    }


    //根据课程先初始化
    private SummaryExportDto initByCoursePo(CoursePo coursePo) {
        SummaryExportDto summaryExportDto = new SummaryExportDto();
        summaryExportDto.setCourseName(coursePo.getName());
        summaryExportDto.setCourseStatusStr(Enums.CourseStatus.code2Desc(coursePo.getCourseStatus()));
        summaryExportDto.setSubject(configService.getTypeMap(Consts.CONFIG_TYPE_科目.intValue()).get(coursePo.getCourseSubject()));
        summaryExportDto.setTotalTimes(coursePo.getTimes());
        summaryExportDto.setPerPrice(Consts.ZERO);
        summaryExportDto.setBookFee(Consts.ZERO);
        summaryExportDto.setGrade(coursePo.getGrade());
        List<CourseExpensePo> courseExpensePos = courseExpenseService.queryByCourseId(coursePo.getId());
        if (!CollectionUtils.isEmpty(courseExpensePos)) {
            courseExpensePos.forEach(courseExpensePo -> {
                if (Consts.FEE_TYPE_普通单节费用.equals(courseExpensePo.getType())) {
                    summaryExportDto.setPerPrice(courseExpensePo.getValue());
                } else {
                    summaryExportDto.setBookFee(courseExpensePo.getValue());
                }
            });
        }
        summaryExportDto.setShouldApplyTotalExpense(
                (NumberUtil.String2Dec(summaryExportDto.getPerPrice()).multiply(new BigDecimal(summaryExportDto.getTotalTimes())))
                        .add(NumberUtil.String2Dec(summaryExportDto.getBookFee())).toPlainString()
        );


        return summaryExportDto;
    }

    private List<CoursePo> getCourseList(SummaryExportReqDto dto, Integer logInUser) {
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser, dto.getCampus());
        CourseSearchDto courseSearchDto = new CourseSearchDto();
        courseSearchDto.setCourseSubject(dto.getCourseSubject());
        courseSearchDto.setCourseType(dto.getCourseType());
        courseSearchDto.setYear(dto.getYear());
        courseSearchDto.setSeason(dto.getSeason());
        courseSearchDto.setGrade(dto.getGrade());
        return courseService.getCoursePos(courseSearchDto, campusList);
    }

    @Override
    public String getFileName() {
        return "班级汇总统计表";
    }

//    @Override
//    public Class getExportClass() {
//        return SummaryExportDto.class;
//    }


    @Override
    protected boolean isWriteSheetBySheet() {
        return true;
    }

    @Override
    protected String getSheetSplitFieldName() {
        return "courseName";
    }

    @Override
    protected String getSheetSortFieldName() {
        return "grade";
    }
}



