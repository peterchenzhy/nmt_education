package com.nmt.education.service;

import com.nmt.education.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class EnumsServiceTest extends BaseTest {

    @Autowired
    private EnumsService enumsService;

    @Test
    void all1() {
        enumsService.all(90000001,"1");
    }
}