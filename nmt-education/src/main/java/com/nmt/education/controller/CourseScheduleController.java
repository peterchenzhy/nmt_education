package com.nmt.education.controller;

import com.nmt.education.pojo.dto.req.CourseScheduleReqDto;
import com.nmt.education.pojo.po.CourseSchedulePo;
import com.nmt.education.pojo.vo.CourseSignInItem;
import com.nmt.education.pojo.vo.CourseSignInVo;
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

    @ApiOperation(value = "signin/default", notes = "课程签到页面--默认页面")
    @RequestMapping(value = "/signIn/default/{courseId}", method = RequestMethod.GET)
    public CourseSignInVo signInDefault(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                        @PathVariable Long courseId) {
        return courseScheduleService.signInDefault(courseId, logInUser);
    }

    @ApiOperation(value = "signin/select", notes = "课程签到页面--选择上课时间下拉列表")
    @RequestMapping(value = "/signIn/select/{courseId}", method = RequestMethod.GET)
    public  List<CourseSchedulePo> signInSelect(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                                @PathVariable Long courseId) {
        return courseScheduleService.signInSelect(courseId, logInUser);
    }

    @ApiOperation(value = "signin/page", notes = "签到列表数据")
    @RequestMapping(value = "/signIn/list/{courseScheduleId}", method = RequestMethod.GET)
    public List<CourseSignInItem> signInList(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                        @PathVariable Long courseScheduleId) {
        return courseScheduleService.signInList(courseScheduleId, logInUser);
    }

    @ApiOperation(value = "signIn", notes = "签到")
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public void signIn(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                           @RequestBody List<CourseSignInItem> list) {
          courseScheduleService.signIn(list, logInUser);
    }

    @ApiOperation(value = "manager", notes = "课表管理")
    @RequestMapping(value = "/manager", method = RequestMethod.POST)
    public Boolean manager(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody List<CourseScheduleReqDto> dtoList, @RequestParam("courseId") Long courseId) {
        return courseScheduleService.manager(dtoList, courseId, logInUser);
    }


}
