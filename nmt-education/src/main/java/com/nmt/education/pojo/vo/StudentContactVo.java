package com.nmt.education.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StudentContactVo {

    @ApiModelProperty(value = "联系方式数据id")
    private Long contractId;
    @ApiModelProperty(value = "名字")
    private String name ;
    @ApiModelProperty(value = "电话")
    private String tel ;
}
