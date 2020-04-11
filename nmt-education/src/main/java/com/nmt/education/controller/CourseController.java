package com.nmt.education.controller;

import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.pojo.dto.req.CourseReqDto;
import com.nmt.education.service.course.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.nmt.education.commmons.Consts.LOGIN_USER_HEAD;
import static com.nmt.education.commmons.Consts.ROLE_ID_HEAD;

/**
 * CourseController
 *
 * @author PeterChen
 * @summary StudentController
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @Description StudentController
 * @since 2020-03-22 22:32
 */
@RestController
@Api(tags = "课程")
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation(value = "manager", notes = "课程编辑提交按钮")
    @RequestMapping(value = "/manager", method = RequestMethod.POST)
    public Boolean newCourse(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                             @RequestBody @Validated CourseReqDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return courseService.courseManager(logInUser, dto);
    }


}
