package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 退款项目
 *
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class RefundReqDto {

    @ApiModelProperty(value = "报名id", required = true)
    @NotNull(message = "报名id不能为空")
    private Long registerId;

    @ApiModelProperty(value = "备注")
    private String remark = "";

    @ApiModelProperty(value = "支付方式")
    @NotNull(message = "支付方式必填")
    private Integer payment;

    @ApiModelProperty(value = "退款项目")
    private List<RefundItemReqDto> itemList;


}