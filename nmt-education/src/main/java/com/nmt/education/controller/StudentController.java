package com.nmt.education.controller;

import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.pojo.dto.req.StudentReqDto;
import com.nmt.education.pojo.dto.req.StudentSearchReqDto;
import com.nmt.education.pojo.vo.StudentAccountVo;
import com.nmt.education.pojo.vo.StudentVo;
import com.nmt.education.service.student.StudentService;
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
    private StudentService studentService;

    @ApiOperation(value = "manager", notes = "学生管理")
    @RequestMapping(value = "/manager", method = RequestMethod.POST)
    public Boolean studentManager(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                  @RequestBody @Validated StudentReqDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return studentService.studentManager(logInUser, dto);
    }


    @ApiOperation(value = "search", notes = "搜索学生")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public PageInfo<StudentVo> searchStudent(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                             @RequestBody @Validated StudentSearchReqDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return studentService.search(logInUser, dto);
    }


    @ApiOperation(value = "search/fuzzy", notes = "学生模糊搜索，左匹配，不含联系方式")
    @RequestMapping(value = "/search/fuzzy", method = RequestMethod.GET)
    public List<StudentVo> searchStudent(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestParam(value = "name") String name) {
        return studentService.searchFuzzy(logInUser, name);
    }


    @ApiOperation(value = "detail", notes = "学生明细")
    @RequestMapping(value = "/detail/{studentId}", method = RequestMethod.POST)
    public StudentVo detail(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long studentId) {
        return studentService.detail(studentId);
    }

    @ApiOperation(value = "amount", notes = "学生账户")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public PageInfo<StudentAccountVo> accountPage(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                             @RequestParam("studentId") Long studentId,
                                             @RequestParam(value = "pageNo" ,required = false ,defaultValue = "1") Integer pageNo,
                                             @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize
                                                  ) {
        return studentService.accountPage(studentId,pageNo,pageSize);
    }

}
