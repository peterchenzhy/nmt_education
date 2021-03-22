package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class CourseSignInReqDto {

    /**
     * 签到
     */
    @NotNull(message = "签到状态不能为空")
    @ApiModelProperty(value = "签到")
    private Integer signIn;

    /**
     * 报名信息id
     */
    @NotNull(message = "报名信息id不能为空")
    @ApiModelProperty(value = "报名信息id")
    private Long registerSummaryId;


}
