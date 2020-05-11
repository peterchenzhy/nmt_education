package com.nmt.education.controller;

import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.pojo.dto.req.*;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.po.CourseRegistrationPo;
import com.nmt.education.pojo.vo.CourseDetailVo;
import com.nmt.education.pojo.vo.CourseRegistrationListVo;
import com.nmt.education.pojo.vo.CourseRegistrationVo;
import com.nmt.education.pojo.vo.RegisterSummaryVo;
import com.nmt.education.service.course.CourseService;
import com.nmt.education.service.course.registeration.CourseRegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@Api(tags = "课程报名")
@RequestMapping("/course")
public class CourseRegisterController {

    @Autowired
    private CourseRegistrationService courseRegistrationService;

    @ApiOperation(value = "register", notes = "报名")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody @Validated CourseRegisterReqDto dto, BindingResult bindingResult) {
        courseRegistrationService.register(dto, logInUser);
    }

    @ApiOperation(value = "register/search", notes = "报名记录查询")
    @RequestMapping(value = "/register/search", method = RequestMethod.POST)
    public PageInfo<CourseRegistrationListVo> registerSearch(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody @Validated RegisterSearchReqDto dto) {
       return  courseRegistrationService.registerSearch(dto, logInUser);
    }

    @ApiOperation(value = "register/detail/{id}", notes = "报名记录详情")
    @RequestMapping(value = "/register/detail/{id}", method = RequestMethod.POST)
    public CourseRegistrationVo registerDetail(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long id ) {
        return  courseRegistrationService.registerDetail(id, logInUser);
    }


    @ApiOperation(value = "register/delete", notes = "退费")
    @RequestMapping(value = "/register/{id}", method = RequestMethod.DELETE)
    public void registerDel(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long id) {
        courseRegistrationService.registerDel(id, logInUser);
    }


    @ApiOperation(value = "register/summary", notes = "课程汇总--消耗查询")
    @RequestMapping(value = "/register/summary", method = RequestMethod.POST)
    public PageInfo<RegisterSummaryVo> registerSummary(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                                       @RequestBody @Validated RegisterSummarySearchDto dto, BindingResult bindingResult) {
        return courseRegistrationService.registerSummary(dto, logInUser);
    }

}
