package com.nmt.education.controller;

import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.Consts;
import com.nmt.education.pojo.dto.req.StudentSearchReqDto;
import com.nmt.education.pojo.dto.req.TeacherReqDto;
import com.nmt.education.pojo.dto.req.TeacherSearchReqDto;
import com.nmt.education.pojo.vo.StudentVo;
import com.nmt.education.pojo.vo.TeacherVo;
import com.nmt.education.service.teacher.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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


    @ApiOperation(value = "new", notes = "新增老师")
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Boolean newStudent(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                              @RequestBody @Validated TeacherReqDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder sb = new StringBuilder();
            errors.stream().forEach(e -> {
                sb.append(e.getDefaultMessage()).append(Consts.分号);
            });
            throw new RuntimeException(sb.toString());
        }
        return teacherService.newTeacher(logInUser, dto);
    }

    @ApiOperation(value = "search", notes = "搜索老师")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public PageInfo<TeacherVo> searchStudent(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                             @RequestBody @Validated TeacherSearchReqDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder sb = new StringBuilder();
            errors.stream().forEach(e -> {
                sb.append(e.getDefaultMessage()).append(Consts.分号);
            });
            throw new RuntimeException(sb.toString());
        }
        return teacherService.search(logInUser, dto);
    }



}
