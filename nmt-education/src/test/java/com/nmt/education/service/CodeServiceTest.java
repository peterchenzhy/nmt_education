package com.nmt.education.service;

import com.nmt.education.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CodeServiceTest extends BaseTest {
@Autowired
private CodeService codeService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void generateNewStudentCode() {
        log.warn(codeService.generateNewStudentCode(1));
    }
}