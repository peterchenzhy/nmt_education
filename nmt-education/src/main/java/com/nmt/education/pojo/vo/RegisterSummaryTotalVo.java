package com.nmt.education.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class RegisterSummaryTotalVo  {


    @ApiModelProperty(value = "总科目")
    private Integer totalCount ;

    @ApiModelProperty(value = "消耗科目")
    private Integer signInCount ;

    @ApiModelProperty(value = "未消耗科目")
    private Integer unSignInCount ;

}
