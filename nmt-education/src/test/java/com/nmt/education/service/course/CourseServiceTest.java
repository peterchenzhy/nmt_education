package com.nmt.education.service.course;

import com.nmt.education.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class CourseServiceTest extends BaseTest {

    @Autowired
    private CourseService courseService;
    @Test
    void finishtest() {
        courseService.finish(1001,8L);
    }
}