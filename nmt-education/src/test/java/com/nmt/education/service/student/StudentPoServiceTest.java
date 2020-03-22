package com.nmt.education.service.student;

import com.alibaba.fastjson.JSON;
import com.nmt.education.BaseTest;
import com.nmt.education.pojo.po.StudentPo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:31
 * @Version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentPoServiceTest extends BaseTest {

    @Autowired
    private StudentPoService studentPoService;

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
        studentPoService.insertSelective(studentPo);
    }

    @Test
    @Order(1)
    void selectByPrimaryKey() {
        System.out.println(JSON.toJSONString(studentPoService.selectByPrimaryKey(1L)));
    }
}