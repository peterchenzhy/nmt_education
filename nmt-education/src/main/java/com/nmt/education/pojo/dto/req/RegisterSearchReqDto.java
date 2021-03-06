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
public class RegisterSearchReqDto extends BaseSearchPageReqDto {

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id")
    private Long studentId;

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
     * 报名日期--开始
     */
    @ApiModelProperty(value = "报名日期--开始")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date signInDateStart;

    /**
     * 报名日期--结束
     */
    @ApiModelProperty(value = "报名日期--结束")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date signInDateEnd;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String orderCode;

    /**
     * 校区
     */
    @ApiModelProperty(value = "校区")
    private Integer campus;

    /**
     * 年度
     */
    @ApiModelProperty(value = "年度")
    private Integer year;

    /**
     * 季度
     */
    @ApiModelProperty(value = "季度")
    private Integer season;


}