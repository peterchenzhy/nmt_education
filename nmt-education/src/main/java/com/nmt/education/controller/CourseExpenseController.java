package com.nmt.education.controller;

import com.nmt.education.pojo.dto.req.CourseExpenseReqDto;
import com.nmt.education.pojo.dto.req.CourseScheduleReqDto;
import com.nmt.education.service.course.expense.CourseExpenseService;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nmt.education.commmons.Consts.LOGIN_USER_HEAD;
import static com.nmt.education.commmons.Consts.ROLE_ID_HEAD;

/**
 * CourseScheduleController
 *
 * @author PeterChen
 * @summary CourseScheduleController
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @Description StudentController
 * @since 2020-03-22 22:32
 */
@RestController
@Api(tags = "课时费")
@RequestMapping("/course/expense")
public class CourseExpenseController {

    @Autowired
    private CourseExpenseService courseExpenseService;

//    @ApiOperation(value = "manager", notes = "课时费管理")
//    @RequestMapping(value = "/manager", method = RequestMethod.POST)
//    public Boolean manager(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
//            , @RequestBody List<CourseExpenseReqDto> dtoList , @RequestParam("courseId") Long courseId ) {
//        return courseExpenseService.manager(dtoList,courseId,logInUser);
//    }



}
