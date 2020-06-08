package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class RegisterExpenseDetailReqDto {

    @ApiModelProperty(value = "编辑标志；0：无变化，1：新增，2：编辑，3：需要删除")
    private Integer editFlag = 0;
    /**
     * 数据id
     */
    @ApiModelProperty(value = "数据id")
    private Long id ;

    /**
     * 费用类型
     */
    @ApiModelProperty(value="费用类型")
    private Integer feeType;

    /**
     * 费用状态
     */
    @ApiModelProperty(value="费用状态")
    private Integer feeStatus;

    /**
     * 费用金额
     */
    @ApiModelProperty(value="费用金额")
    private String amount;

    /**
     * 单项金额
     */
    @ApiModelProperty(value="单项金额")
    private String perAmount;

    /**
     * 数量
     */
    @ApiModelProperty(value="数量")
    private Integer count;

    /**
     * 折扣
     */
    @ApiModelProperty(value="折扣")
    private String discount;

    /**
     * 缴费方式
     */
    @ApiModelProperty(value="缴费方式")
    private Integer payment;

    @ApiModelProperty(value = "备注")
    private String remark="";
}
