package com.nmt.education.controller;

import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.pojo.dto.req.CourseRegisterReqDto;
import com.nmt.education.pojo.dto.req.CourseReqDto;
import com.nmt.education.pojo.dto.req.CourseSearchDto;
import com.nmt.education.pojo.dto.req.RegisterSummarySearchDto;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.vo.CourseDetailVo;
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
@Api(tags = "课程")
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseRegistrationService courseRegistrationService;

    @ApiOperation(value = "manager", notes = "课程编辑提交按钮")
    @RequestMapping(value = "/manager", method = RequestMethod.POST)
    public Boolean courseManager(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                 @RequestBody @Validated CourseReqDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return courseService.courseManager(logInUser, dto);
    }


    @ApiOperation(value = "search", notes = "课程查询")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public PageInfo<CoursePo> search(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                     @RequestBody @Validated CourseSearchDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return courseService.search(dto);
    }

    @ApiOperation(value = "/search/fuzzy", notes = "根据课程编号或者课程名称，课程模糊查询")
    @RequestMapping(value = "/search/fuzzy", method = RequestMethod.GET)
    public List<CoursePo> searchFuzzy(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                            @RequestParam String name) {
        return courseService.searchFuzzy(name);
    }

    @ApiOperation(value = "detail", notes = "课程明细")
    @RequestMapping(value = "/detail/{courseId}", method = RequestMethod.POST)
    public CourseDetailVo detail(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long courseId) {
        return courseService.detail(courseId);
    }


    @ApiOperation(value = "register", notes = "报名")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody @Validated CourseRegisterReqDto dto, BindingResult bindingResult) {
        courseRegistrationService.register(dto,logInUser);
    }

    @ApiOperation(value = "register/delete", notes = "退费")
    @RequestMapping(value = "/register/{id}", method = RequestMethod.DELETE)
    public void registerDel(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long id) {
        courseRegistrationService.registerDel(id,logInUser);
    }


    @ApiOperation(value = "register/summary", notes = "课程汇总--消耗查询")
    @RequestMapping(value = "/register/summary", method = RequestMethod.POST)
    public PageInfo<RegisterSummaryVo> registerSummary(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                                       @RequestBody @Validated RegisterSummarySearchDto dto, BindingResult bindingResult)  {
       return courseRegistrationService.registerSummary(dto,logInUser);
    }

}
