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
public class TeacherSearchReqDto {

    /**
     * 学生姓名
     */
    @ApiModelProperty(value = "老师姓名")
    private String name;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "分页 页码")
    private Integer pageNo = 1;

    @ApiModelProperty(value = "分页 每页数量")
    private Integer pageSize = 10;


}