package com.nmt.education.service.student.account;

import com.nmt.education.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class StudentAccountServiceTest extends BaseTest {

    @Autowired
    private StudentAccountService studentAccountService;
    @Test
    void queryAccount1() {
        System.out.println( studentAccountService.queryAccount(null));
    }
}