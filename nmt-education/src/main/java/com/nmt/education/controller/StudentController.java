package com.nmt.education.controller;

import com.nmt.education.commmons.Consts;
import com.nmt.education.pojo.dto.req.StudentReqDto;
import com.nmt.education.service.student.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nmt.education.commmons.Consts.LOGIN_USER_HEAD;
import static com.nmt.education.commmons.Consts.ROLE_ID_HEAD;

/**
 * StudentController
 *
 * @author PeterChen
 * @summary StudentController
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @Description StudentController
 * @since 2020-03-22 22:32
 */
@RestController
@Api(tags = "学生")
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService studentService;

    @ApiOperation(value = "new", notes = "新增学生")
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Boolean newStudent(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                              @RequestBody @Validated StudentReqDto studentReqDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<ObjectError> errors =bindingResult.getAllErrors();
            StringBuilder sb = new StringBuilder();
            errors.stream().forEach(e->{
                sb.append(e.getDefaultMessage()).append(Consts.分号);
            });
            throw new RuntimeException(sb.toString());
        }
        return studentService.newStudent(logInUser, studentReqDto);
    }
}
