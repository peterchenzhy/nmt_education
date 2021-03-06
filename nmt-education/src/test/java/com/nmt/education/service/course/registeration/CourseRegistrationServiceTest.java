package com.nmt.education.service.course.registeration;

import com.google.common.collect.Lists;
import com.nmt.education.BaseTest;
import com.nmt.education.commmons.Enums;
import com.nmt.education.pojo.dto.req.CourseRegisterReqDto;
import com.nmt.education.pojo.dto.req.RegisterExpenseDetailReqDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class CourseRegistrationServiceTest extends BaseTest {
    @Autowired
    private CourseRegistrationService registrationService ;

    @Test
    void startRegisterTransaction() {
        CourseRegisterReqDto dto = new CourseRegisterReqDto();
        dto.setStudentId(1L);
        dto.setCourseId(7L);
        dto.setRegistrationType(Enums.RegistrationType.新报.getCode());
        dto.setRegistrationStatus(Enums.RegistrationStatus.正常.getCode());
        dto.setFeeStatus(Enums.FeeStatus.已缴费.getCode());
        dto.setRemark("remark 123455");
        dto.setCourseScheduleIds(Lists.newArrayList(25L));
        RegisterExpenseDetailReqDto detailReqDto = new RegisterExpenseDetailReqDto();
        detailReqDto.setFeeType(1);
        detailReqDto.setFeeStatus(Enums.FeeStatus.已缴费.getCode());
        detailReqDto.setAmount("200");
        detailReqDto.setPerAmount("20");
        detailReqDto.setCount(10);
        detailReqDto.setDiscount("1");
        detailReqDto.setPayment(1);
        detailReqDto.setRemark("StringUtils.EMPTY");

        dto.setRegisterExpenseDetail(Lists.newArrayList(detailReqDto));

        registrationService.startRegisterTransaction(dto,10086);
    }

    @Test
    void queryByCourseStudent() {
        registrationService.queryByCourseStudent(1L,1L);
    }

    @Test
    void registerDetail() {
        System.out.println(registrationService.registerDetail(1L,1));
    }

    @Test
    void registerSummaryByRegisterId1() {
        System.out.println(registrationService.registerSummaryByRegisterId(2L,1,1,5));
    }

    @Test
    void countStudentByCourse1() {
        System.out.println(   registrationService.countStudentByCourse( Lists.newArrayList(1L,2L)));
    }
}