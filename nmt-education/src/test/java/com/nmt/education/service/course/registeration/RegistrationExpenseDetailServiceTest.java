package com.nmt.education.service.course.registeration;

import com.nmt.education.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationExpenseDetailServiceTest extends BaseTest {

    @Autowired
    private RegistrationExpenseDetailService registrationExpenseDetailService;
    @Test
    void getExpenseDetailFlowVo1() {
        System.out.println(registrationExpenseDetailService.getExpenseDetailFlowVo(23L));
    }
}