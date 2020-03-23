package com.nmt.education.controller;

import com.nmt.education.pojo.vo.EnumVo;
import com.nmt.education.service.EnumsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * EnumsController 枚举controller
 *
 * @author PeterChen
 * @summary AdminControlle
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @Description EnumsController
 * @since 2020-03-22 22:32
 */
@RestController
@Api(tags = "枚举接口")
@RequestMapping("/enums")
public class EnumsController {


    @Autowired
    private EnumsService enumsService;

    @ApiOperation(value = "all",notes = "全部枚举接口")
    @RequestMapping(value = "/all",method = RequestMethod.GET)
    public  Map<String, List<EnumVo>> enums() {

        return enumsService.all();
    }
}
