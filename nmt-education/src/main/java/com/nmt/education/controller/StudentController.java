package com.nmt.education.controller;

import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.NumberUtil;
import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.commmons.utils.RoleUtils;
import com.nmt.education.pojo.dto.req.AccountEditReqDto;
import com.nmt.education.pojo.dto.req.StudentReqDto;
import com.nmt.education.pojo.dto.req.StudentSearchReqDto;
import com.nmt.education.pojo.vo.StudentAccountDetailVo;
import com.nmt.education.pojo.vo.StudentAccountVo;
import com.nmt.education.pojo.vo.StudentVo;
import com.nmt.education.service.student.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
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

    @ApiOperation(value = "accountPage", notes = "学生账户分页接口")
    @RequestMapping(value = "/accountPage", method = RequestMethod.GET)
    public PageInfo<StudentAccountVo> accountPage(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                                  @RequestParam(value = "studentId", required = false) Long studentId,
                                                  @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        return studentService.accountPage(studentId, pageNo, pageSize);
    }

    @ApiOperation(value = "account", notes = "学生账户")
    @RequestMapping(value = "/account/{studentId}", method = RequestMethod.POST)
    public StudentAccountVo account(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long studentId) {
        return studentService.account(studentId);
    }

    @ApiOperation(value = "account", notes = "学生账户明细")
    @RequestMapping(value = "/account/detail/{studentId}", method = RequestMethod.POST)
    public PageInfo<StudentAccountDetailVo> accountDetail(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long studentId,
                                                          @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                          @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return studentService.accountDetail(studentId,pageNo,pageSize);
    }


    @ApiOperation(value = "accountEdit", notes = "学生账户编辑")
    @RequestMapping(value = "/account/edit", method = RequestMethod.POST)
    public void account(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody @Validated AccountEditReqDto accountEditReqDto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        RoleUtils.check校长财务(roleId);
        Assert.isTrue(NumberUtil.isAmount(accountEditReqDto.getAmount()), "账户金额不正确！" + accountEditReqDto.getAmount());
        studentService.accountEdit(accountEditReqDto, logInUser);
    }
}
