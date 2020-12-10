package com.nmt.education.pojo.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 费用统计 查询
 */
@Data
public class FeeStatisticsReqDto extends BaseSearchPageReqDto {
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate ;

    @ApiModelProperty(value = "结束日期")
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate ;

    @ApiModelProperty(value = "课程所在校区")
    private Integer campus;

    @ApiModelProperty(value = "年度")
    private Integer year;

    @ApiModelProperty(value = "季节")
    private Integer season;

    @ApiModelProperty(value="费用类型 1：付费 2：退费" )
    private Integer feeFlowType;

}
