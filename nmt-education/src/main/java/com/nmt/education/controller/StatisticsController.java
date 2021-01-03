package com.nmt.education.controller;

import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.RoleIdEnum;
import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.pojo.dto.req.FeeStatisticsReqDto;
import com.nmt.education.pojo.vo.FeeSummaryVo;
import com.nmt.education.service.statistics.FeeStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
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
    public PageInfo feeStatistics(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                  @RequestParam(value = "isManager", required = false, defaultValue = "false") Boolean isManager
            , @RequestBody @Validated FeeStatisticsReqDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        baseCheck(logInUser, roleId, isManager, dto);
        return feeStatisticsService.page(dto, logInUser);
    }

    @ApiOperation(value = "费用统计--收费，退费，课时费", notes = "费用统计--收费，退费，课时费")
    @RequestMapping(value = "/fee/summary", method = RequestMethod.POST)
    public FeeSummaryVo feeSummary(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                   @RequestParam(value = "isManager", required = false, defaultValue = "false") Boolean isManager
            , @RequestBody @Validated FeeStatisticsReqDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        baseCheck(logInUser, roleId, isManager, dto);
        return feeStatisticsService.summary(dto, logInUser, isManager);
    }

    private void baseCheck(Integer logInUser,  String roleId, Boolean isManager, FeeStatisticsReqDto dto) {
        if (isManager) {
            Assert.isTrue(Integer.valueOf(roleId).intValue() == RoleIdEnum.校长.getCode()
                    || Integer.valueOf(roleId).intValue() == RoleIdEnum.财务.getCode(), "您没有该功能权限");
        }
        if (Integer.valueOf(roleId).intValue() == RoleIdEnum.员工.getCode()) {
            Assert.notNull( dto.getUserCode(), "员工号不能为空");
            Assert.isTrue(dto.getUserCode().equals(logInUser), "只能查询本人数据");
        }
    }

}
