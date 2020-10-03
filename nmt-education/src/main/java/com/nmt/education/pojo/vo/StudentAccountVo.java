package com.nmt.education.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StudentAccountVo {
    /**
     * 主键id
     */
    @ApiModelProperty(value="主键id")
    private Long id;

    /**
     * 员工id
     */
    @ApiModelProperty(value="学生id")
    private Long studentId;

    /**
     * 工号
     */
    @ApiModelProperty(value="学生code")
    private String studentCode;

    /**
     * 工号
     */
    @ApiModelProperty(value="学生名称")
    private String studentName;

    /**
     * 金额
     */
    @ApiModelProperty(value="金额")
    private String amount;

    /**
     * 有效：1 无效：0
     */
    @ApiModelProperty(value="有效：1 无效：0")
    private Integer status;

    /**
     * 备注
     */
    @ApiModelProperty(value="备注")
    private String remark;

}
