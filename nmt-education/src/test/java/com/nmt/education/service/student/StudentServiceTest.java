package com.nmt.education.service.student;

import com.alibaba.fastjson.JSON;
import com.nmt.education.BaseTest;
import com.nmt.education.pojo.dto.req.AccountEditReqDto;
import com.nmt.education.pojo.dto.req.StudentReqDto;
import com.nmt.education.pojo.po.StudentPo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.spring.web.json.Json;

import java.util.Date;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:31
 * @Version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
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

    @Test
    void editStudent() {
        StudentReqDto dto = new StudentReqDto();
        dto.setId(1L);
        dto.setName("HULU");
        dto.setBirthday(new Date());
        dto.setSchool("waterluu");
        dto.setGrade(5);
        dto.setCampus(1);
        dto.setPhone("133434");
        dto.setSex(1);
        dto.setRemark("remark");
        System.out.println( studentService.editStudent(loginUser,dto) );
    }

    @Test
    void accountPage1() {
        log.warn(JSON.toJSONString(this.studentService.accountPage(null,1,10)));
    }

    @Test
    void accountEdit1() {
        AccountEditReqDto accountEditReqDto = new AccountEditReqDto();
        accountEditReqDto.setStudentId(5L);
        accountEditReqDto.setAmount("1500");
        accountEditReqDto.setRemark("test2");
        studentService.accountEdit(accountEditReqDto,loginUser);
    }
}