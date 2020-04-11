package com.nmt.education.controller;

import com.nmt.education.pojo.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "用户服务")
@RequestMapping("/user")
public class UserController {


    @ApiOperation(value = "login", notes = "登录服务，mock数据不做校验")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public UserVo searchFuzzy(@RequestParam(value = "userName") String name,
                              @RequestParam(value = "password") String password) {
        return new UserVo();
    }
}
