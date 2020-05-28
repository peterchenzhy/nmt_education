package com.nmt.education.service.course.schedule;

import com.nmt.education.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sound.midi.Soundbank;

import static org.junit.jupiter.api.Assertions.*;

class CourseScheduleServiceTest extends BaseTest {

    @Autowired
    private CourseScheduleService courseScheduleService;
    @Test
    void signInDefault1() {
        System.out.println(courseScheduleService.signInDefault(7L,0));
    }
}