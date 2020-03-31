package com.nmt.education.service.student;

import com.alibaba.fastjson.JSON;
import com.nmt.education.BaseTest;
import com.nmt.education.pojo.dto.req.StudentReqDto;
import com.nmt.education.pojo.po.StudentPo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:31
 * @Version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentServiceTest extends BaseTest {

    @Autowired
    private StudentService studentService;

    int loginUser = 1000336 ;

    @Test
    @Order(0)
    void insertSelective() {
        StudentPo studentPo = new StudentPo();
        studentPo.setStudentCode("111223");
        studentPo.setName("AbSDc");
        studentPo.setBirthday(new Date());
        studentPo.setSchool("小学");
        studentPo.setGrade(-1);
        studentPo.setRemark("2321");
        studentPo.setStatus(1);
        studentPo.setPhone("1333123");
        studentPo.setSex(1);
        studentPo.setCreator(1);
        studentPo.setCreateTime(new Date());
        studentPo.setOperator(1);
        studentPo.setOperateTime(new Date());
        studentService.insertSelective(studentPo);
    }

    @Test
    @Order(1)
    void selectByPrimaryKey() {
        System.out.println(JSON.toJSONString(studentService.selectByPrimaryKey(1L)));
    }

    @Test
    void newStudent() {
        StudentReqDto req = new StudentReqDto();
        req.setName("peter");
        req.setBirthday(null);
        req.setSchool("大兴小学");
        req.setGrade(2);
        req.setCampus(1);
        req.setPhone("13774403");
        req.setSex(1);
        req.setRemark(null);
        this.studentService.newStudent(loginUser,req);
    }
}