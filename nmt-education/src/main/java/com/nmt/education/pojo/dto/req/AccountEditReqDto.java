package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;


@Data
public class AccountEditReqDto {
    /**
     * 员工id
     */
    @ApiModelProperty(value="学生id",required = true)
    @NotNull(message = "学生姓名不能为空")
    private Long studentId;
    /**
     * 金额
     */
    @ApiModelProperty(value="金额",required = true)
    @NotNull(message = "金额不能为空")
    private String amount;

    @NotNull(message = "备注不能为空")
    @ApiModelProperty(value = "备注", required = true)
    @Length(max = 50, message = "备注最多50个字")
    private String remark ;
}
