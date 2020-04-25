package com.nmt.education.controller;

import com.nmt.education.pojo.dto.req.CourseScheduleReqDto;
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
@Api(tags = "课表")
@RequestMapping("/course/schedule")
public class CourseScheduleController {

    @Autowired
    private CourseScheduleService courseScheduleService;


    @ApiOperation(value = "signIn", notes = "课表签到")
    @RequestMapping(value = "/signIn/{id}", method = RequestMethod.POST)
    public Boolean detail(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
    ,@PathVariable Long id ) {
        return courseScheduleService.signIn(id,logInUser);
    }

    @ApiOperation(value = "manager", notes = "课表管理")
    @RequestMapping(value = "/manager", method = RequestMethod.POST)
    public Boolean manager(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody List<CourseScheduleReqDto> dtoList ,@RequestParam("courseId") Long courseId ) {
        return courseScheduleService.manager(dtoList,courseId,logInUser);
    }



}
