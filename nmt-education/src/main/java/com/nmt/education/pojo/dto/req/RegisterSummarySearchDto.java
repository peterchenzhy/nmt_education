package com.nmt.education.pojo.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class RegisterSummarySearchDto {

    /**
     * 课程科目
     */
    @ApiModelProperty(value = "课程科目")
    private Integer courseSubject;

    /**
     * 课程类型
     */
    @ApiModelProperty(value = "课程类型")
    private Integer courseType;
    /**
     * 年级
     */
    @ApiModelProperty(value = "年级")
    private Integer grade;

    /**
     * 上课日期 -- 开始
     */
    @ApiModelProperty(value = "上课日期 -- 开始")
    @JsonFormat( pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    /**
     * 上课日期-- 结束
     */
    @ApiModelProperty(value = "上课日期-- 结束")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    /**
     * 报名日期 -- 开始
     */
    @ApiModelProperty(value = "报名日期 -- 开始")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date registerStartDate;
    /**
     * 报名日期-- 结束
     */
    @ApiModelProperty(value = "报名日期-- 结束")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date registerEndDate;

    /**
     * 签到状态
     */
    @ApiModelProperty(value = "签到状态")
    private Integer signIn;

    @ApiModelProperty(value = "分页 页码")
    private Integer pageNo = 0;

    @ApiModelProperty(value = "分页 每页数量")
    private Integer pageSize = 10;


}