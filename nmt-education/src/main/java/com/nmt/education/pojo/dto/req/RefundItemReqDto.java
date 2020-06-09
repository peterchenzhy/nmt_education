package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 退款项目
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class RefundItemReqDto {

    @ApiModelProperty(value = "报名id",required = true)
    @NotNull(message = "报名id不能为空")
    private Long registerId;

    @ApiModelProperty(value = "报名课时记录id")
    private Long registerSummaryId;

    @ApiModelProperty(value = "费用类型",required = true)
    @NotNull(message = "费用类型必传")
    private Integer feeType ;

    @ApiModelProperty(value = "费用金额",required = true)
    @NotNull(message = "费用金额必传")
    private String amount ;


}