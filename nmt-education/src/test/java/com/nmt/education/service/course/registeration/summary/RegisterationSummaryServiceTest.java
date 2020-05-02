package com.nmt.education.service.course.registeration.summary;

import com.nmt.education.BaseTest;
import com.nmt.education.pojo.dto.req.RegisterSummarySearchDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class RegisterationSummaryServiceTest extends BaseTest {

    @Autowired
    private RegisterationSummaryService registerationSummaryService;
    @Test
    void queryBySearchDto() {
        RegisterSummarySearchDto dto = new RegisterSummarySearchDto();
        System.out.println(registerationSummaryService.queryBySearchDto(dto));
    }
}