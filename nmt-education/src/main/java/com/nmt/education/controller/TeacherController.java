package com.nmt.education.controller;

import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.pojo.dto.req.TeacherReqDto;
import com.nmt.education.pojo.dto.req.TeacherSearchReqDto;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.vo.TeacherVo;
import com.nmt.education.service.teacher.TeacherService;
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
 * Teacher
 * Controller
 *
 * @author PeterChen
 * @summary StudentController
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @Description StudentController
 * @since 2020-03-22 22:32
 */
@RestController
@Api(tags = "老师")
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;


    @ApiOperation(value = "search/fuzzy", notes = "老师模糊搜索，左匹配，不含联系方式")
    @RequestMapping(value = "/search/fuzzy", method = RequestMethod.GET)
    public List<TeacherVo> searchFuzzy(@RequestParam(value = "name") String name) {
        return teacherService.searchFuzzy(name);
    }


    @ApiOperation(value = "manager", notes = "教师管理")
    @RequestMapping(value = "/manager", method = RequestMethod.POST)
    public Boolean newTeacher(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                              @RequestBody @Validated TeacherReqDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return teacherService.teacherManager(logInUser, dto);
    }


    @ApiOperation(value = "search", notes = "搜索老师")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public PageInfo<TeacherVo> searchTeacher(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                             @RequestBody @Validated TeacherSearchReqDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return teacherService.search(logInUser, dto);
    }


    @ApiOperation(value = "detail", notes = "老师明细")
    @RequestMapping(value = "/detail/{teacherId}", method = RequestMethod.POST)
    public TeacherVo detail(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long teacherId) {
        return teacherService.detail(teacherId);
    }

    @ApiOperation(value = "course", notes = "老师授课明细")
    @RequestMapping(value = "/course/list/{teacherId}", method = RequestMethod.POST)
    public PageInfo<CoursePo> courseList(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long teacherId, @RequestParam(value = "pageNo", required = false, defaultValue = "0") Integer pageNo, @RequestParam(value = "pageSize", required = false, defaultValue = "0") Integer pageSize) {
        return teacherService.courseList(teacherId,pageNo,pageSize);
    }

}
