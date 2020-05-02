package com.nmt.education.service.user;

import com.nmt.education.BaseTest;
import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.po.UserPo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.UpperCase;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sound.midi.Soundbank;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;
    @Test
    void insertSelective() {
        UserPo userPo = new UserPo();
        userPo.setName("yan老师");
        userPo.setPassword("123");
        userPo.setCode(10000002);
        userPo.setTel("");
        userPo.setStatus(StatusEnum.VALID.getCode());
        userPo.setRemark("");
        userPo.setCreator(Consts.SYSTEM_USER);
        userPo.setCreateTime(new Date());
        userPo.setOperator(Consts.SYSTEM_USER);
        userPo.setOperateTime(new Date());
        System.out.println(userService.insertSelective(userPo));
    }
}