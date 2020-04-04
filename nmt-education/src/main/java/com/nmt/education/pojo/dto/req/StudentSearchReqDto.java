package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class StudentSearchReqDto {

    /**
    * 学生姓名
    */
    @NotNull(message = "学生姓名不能为空")
    @ApiModelProperty(value = "学生姓名")
    private String name;

    /**
    * 学校
    */
    @ApiModelProperty(value = "学校")
    private String school;

    /**
    * 年级 小学初中1-9 高中10-12 
    */
    @NotNull(message = "年级不能为空")
    @Range(min = 1, message = "年级必须大于0")
    @ApiModelProperty(value = "年级")
    private Integer grade;
    /**
     * 所在校区
     */
    @NotNull(message = "所在校区不能为空")
    @ApiModelProperty(value = "所在校区")
    @Range(min = 1, message = "所在校区必须大于0")
    private Integer campus;

    /**
    * 电话
    */
    @ApiModelProperty(value = "电话")
    private String phone;



}