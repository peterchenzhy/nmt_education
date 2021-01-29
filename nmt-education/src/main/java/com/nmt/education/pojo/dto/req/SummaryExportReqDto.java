package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class SummaryExportReqDto {

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

    @ApiModelProperty(value = "年级")
    private Integer grade;

    @ApiModelProperty(value = "季度")
    private Integer season;
    @ApiModelProperty(value = "年度")
    private Integer year;

    @ApiModelProperty(value = "校区")
    private Integer campus;




}