package com.nmt.education.controller;

import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.commmons.utils.RoleUtils;
import com.nmt.education.pojo.dto.req.FeeStatisticsReqDto;
import com.nmt.education.pojo.dto.req.TeacherScheduleReqDto;
import com.nmt.education.service.export.FeeStatisticsExportService;
import com.nmt.education.service.export.ScheduleTeacherExportService;
import com.nmt.education.service.export.TeacherSalarySummaryExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
    private FeeStatisticsExportService feeStatisticsExportService;
    @Autowired
    private ScheduleTeacherExportService scheduleTeacher;
    @Autowired
    private TeacherSalarySummaryExportService teacherSalarySummaryExportService;

    @ApiOperation(value = "费用统计报表导出", notes = "费用统计报表导出")
    @RequestMapping(value = "/feeStatistics", method = RequestMethod.POST)
    public void feeStatistics(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody @Validated FeeStatisticsReqDto dto, BindingResult bindingResult, HttpServletResponse response) throws IOException {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        RoleUtils.check校长财务(roleId);
        feeStatisticsExportService.doExport(dto, logInUser, response);
    }

    @ApiOperation(value = "课程明细统计", notes = "课程明细统计")
    @RequestMapping(value = "/schedule/teacher", method = RequestMethod.POST)
    public void scheduleTeacher(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody @Validated TeacherScheduleReqDto dto, BindingResult bindingResult, HttpServletResponse response) throws IOException {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        RoleUtils.check校长财务(roleId);
        scheduleTeacher.doExport(dto, logInUser, response);
    }

    @ApiOperation(value = "教师课时费统计", notes = "教师课时费统计")
    @RequestMapping(value = "/schedule/teacher/salary", method = RequestMethod.POST)
    public void scheduleTeacherSalary(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @RequestBody @Validated TeacherScheduleReqDto dto, BindingResult bindingResult, HttpServletResponse response) throws IOException {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        RoleUtils.check校长财务(roleId);
        teacherSalarySummaryExportService.doExport(dto, logInUser, response);
    }



}
