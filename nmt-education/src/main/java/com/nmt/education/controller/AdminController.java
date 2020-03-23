package com.nmt.education.controller;

import com.alibaba.fastjson.JSON;
import com.nmt.education.service.student.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AdminController
 *
 * @author PeterChen
 * @summary AdminControlle
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @Description AdminControlle
 * @since 2020-03-22 22:32
 */
@RestController
@Api(tags = "自定义接口")
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private StudentService studentService;

    @ApiOperation(value = "test",notes = "swagger测试接口")
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String test(@RequestParam(value = "testparam", required = false, defaultValue = "") String testparam) {
        return JSON.toJSONString(studentService.selectByPrimaryKey(1L));
    }


}
