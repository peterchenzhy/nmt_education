package com.nmt.education.pojo.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 费用统计 查询
 */
@Data
public class TeacherScheduleReqDto extends BaseSearchPageReqDto {
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @ApiModelProperty(value = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(value = "课程所在校区")
    private Integer campus;

}
