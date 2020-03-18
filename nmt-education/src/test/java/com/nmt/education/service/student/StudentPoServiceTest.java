package com.nmt.education.service.student;

import com.alibaba.fastjson.JSON;
import com.nmt.education.BaseTest;
import com.nmt.education.pojo.po.StudentPo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;

import javax.sound.midi.Soundbank;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:31
 * @Version 1.0
 */
class StudentPoServiceTest extends BaseTest {

    @Autowired
    private StudentPoService studentPoService;

    @Test
    void insertSelective() {
        StudentPo studentPo = new StudentPo();
        studentPo.setStudentCode("111");
        studentPo.setName("Abc");
        studentPo.setBirthday(-1);
        studentPo.setSchool("小学");
        studentPo.setGrade(-1);
        studentPo.setContact1("tony");
        studentPo.setContact2("王阿姨");
        studentPo.setPhone1("13817899090");
        studentPo.setPhone2("12");
        studentPo.setRemark("2321");
        studentPo.setStatus(1);
        studentPo.setCreator(1);
        studentPo.setCreateTime(new Date());
        studentPo.setOperator(1);
        studentPo.setOperateTime(new Date());
        studentPoService.insertSelective(studentPo);
    }

    @Test
    void selectByPrimaryKey() {
        System.out.println(JSON.toJSONString(studentPoService.selectByPrimaryKey(1L)));
    }
}