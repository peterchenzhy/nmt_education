package com.nmt.education.controller;

import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.pojo.dto.req.FeeStatisticsReqDto;
import com.nmt.education.service.statistics.FeeStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.nmt.education.commmons.Consts.LOGIN_USER_HEAD;
import static com.nmt.education.commmons.Consts.ROLE_ID_HEAD;

/**
 * StatisticsController
 *
 * @author PeterChen
 * @summary AdminControlle
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @Description AdminControlle
 * @since 2020-03-22 22:32
 */
@RestController
@Api(tags = "报表统计")
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private FeeStatisticsService feeStatisticsService;

    @ApiOperation(value = "费用统计", notes = "费用统计")
    @RequestMapping(value = "/fee", method = RequestMethod.POST)
    public PageInfo feeStatistics(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody @Validated FeeStatisticsReqDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return feeStatisticsService.page(dto, logInUser);
    }


}
