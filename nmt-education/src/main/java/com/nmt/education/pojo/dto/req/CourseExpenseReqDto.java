package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 课程费用列表
 */
@Getter
@Setter
@ToString
public class CourseExpenseReqDto {

    @ApiModelProperty(value = "数据id,仅编辑使用")
    private Long id;
    @ApiModelProperty(value = "编辑标志；0：无变化，1：新增，2：编辑，3：需要删除")
    private Integer editFlag = 0;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Long courseId;
    /**
     * 费用类型
     */
    @ApiModelProperty(value = "费用类型")
    private Integer type;

    /**
     * 费用
     */
    @ApiModelProperty(value = "费用")
    private String price;
}