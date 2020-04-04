package com.nmt.education.service.teacher;

import com.nmt.education.BaseTest;
import com.nmt.education.pojo.dto.req.TeacherReqDto;
import com.nmt.education.pojo.po.TeacherPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TeacherServiceTest extends BaseTest {

    @Autowired
    private TeacherService teacherService;

    @Test
    void newTeacher() {
        TeacherReqDto teacherReqDto = new TeacherReqDto();
        teacherReqDto.setName("王老四");
        teacherReqDto.setBirthday(new Date());
        teacherReqDto.setSchool("华二初中部张江集团吧啦啦啦啦");
        teacherReqDto.setPhone("123211323");
        teacherReqDto.setSex(0);
        teacherReqDto.setRemark("adasdas 阿达阿达的");
        teacherService.newTeacher(1,teacherReqDto);
    }
}