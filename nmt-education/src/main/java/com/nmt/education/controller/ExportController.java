package com.nmt.education.controller;

import com.nmt.education.commmons.RoleIdEnum;
import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.pojo.dto.req.FeeStatisticsReqDto;
import com.nmt.education.service.ExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.nmt.education.commmons.Consts.LOGIN_USER_HEAD;
import static com.nmt.education.commmons.Consts.ROLE_ID_HEAD;

/**
 * DownloadController 下载
 *
 * @author PeterChen
 * @summary DownloadController
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @Description AdminControlle
 * @since 2020-03-22 22:32
 */
@RestController
@Api(tags = "下载")
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @ApiOperation(value = "费用统计报表导出", notes = "费用统计")
    @RequestMapping(value = "/feeStatistics", method = RequestMethod.POST)
    public void feeStatistics(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody @Validated FeeStatisticsReqDto dto, BindingResult bindingResult, HttpServletResponse response) throws IOException {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        Assert.isTrue(Integer.valueOf(roleId).intValue() == RoleIdEnum.校长.getCode()
                || Integer.valueOf(roleId).intValue() == RoleIdEnum.财务.getCode(), "您没有该功能权限");
        exportService.exportFeeStatistics(dto, logInUser, response);
    }


}
