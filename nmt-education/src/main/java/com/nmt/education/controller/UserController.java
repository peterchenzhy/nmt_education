package com.nmt.education.controller;

import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.pojo.dto.req.UserLoginDto;
import com.nmt.education.pojo.vo.UserVo;
import com.nmt.education.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "用户服务")
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @ApiOperation(value = "login", notes = "登录服务")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public UserVo login(@RequestBody @Validated UserLoginDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return userService.login(dto);
    }
    @RequestMapping(value = "/update/password", method = RequestMethod.POST)
    private String updatePassword( @RequestBody Map<String,String> map){
        return userService.updatePassword(map);
    }

    @ApiOperation(value = "getUsers", notes = "获取用户")
    @RequestMapping(value = "/getUsers", method = RequestMethod.GET)
    public List<UserVo> getUsers() {
        return userService.getUsers();
    }

}
