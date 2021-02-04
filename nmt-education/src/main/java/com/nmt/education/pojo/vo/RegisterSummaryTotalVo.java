package com.nmt.education.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class RegisterSummaryTotalVo  {


    @ApiModelProperty(value = "总课次")
    private Integer totalCount ;

    @ApiModelProperty(value = "消耗课次")
    private Integer signInCount ;

    @ApiModelProperty(value = "未消耗课次")
    private Integer unSignInCount ;
    @ApiModelProperty(value = "报名人数")
    private long registerStudentCount;

    @ApiModelProperty(value = "总科目")
    private long registerCount;



}
