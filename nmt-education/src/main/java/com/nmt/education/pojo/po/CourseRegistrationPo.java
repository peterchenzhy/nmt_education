package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "com-nmt-education-pojo-po-CourseRegistrationPo")
@Getter
@Setter
@ToString
public class CourseRegistrationPo {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String registrationNumber;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Long courseId;

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id")
    private Long studentId;

    /**
     * 报名课次
     */
    @ApiModelProperty(value = "报名课次")
    private Integer times;

    /**
     * 报名类型
     */
    @ApiModelProperty(value = "报名类型")
    private Integer registrationType;

    /**
     * 订单状态
     */
    @ApiModelProperty(value = "订单状态")
    private Integer registrationStatus;

    /**
     * 费用状态
     */
    @ApiModelProperty(value = "费用状态")
    private Integer feeStatus;

    /**
     * 有效：1 无效：0
     */
    @ApiModelProperty(value = "有效：1 无效：0")
    private Integer status;

    /**
     * 订单余额
     */
    @ApiModelProperty(value = "订单余额")
    private String balanceAmount;

    /**
     * 订单总额
     */
    @ApiModelProperty(value = "订单总额")
    private String totalAmount;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private Integer creator;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更改人
     */
    @ApiModelProperty(value = "更改人")
    private Integer operator;

    /**
     * 更改时间
     */
    @ApiModelProperty(value = "更改时间")
    private Date operateTime;
}