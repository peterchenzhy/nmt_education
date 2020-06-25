package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseSearchPageReqDto {
    
    @ApiModelProperty(value = "分页 页码")
    private Integer pageNo = 1;
    @ApiModelProperty(value = "分页 每页数量")
    private Integer pageSize = 10;
}
