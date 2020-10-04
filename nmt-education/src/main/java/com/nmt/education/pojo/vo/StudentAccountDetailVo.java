package com.nmt.education.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@Getter
@Setter
@ToString
public class StudentAccountDetailVo {


    @ApiModelProperty(value="学生id")
    private Long studentId ;
    @ApiModelProperty(value="学生姓名")
    private String studentName;
    /**
     * 主键id
     */
    @ApiModelProperty(value="主键id")
    private Long id;

    /**
     * 账户id
     */
    @ApiModelProperty(value="账户id")
    private Long studentAccountId;

    /**
     * 操作类型
     */
    @ApiModelProperty(value="操作类型")
    private String type;

    /**
     * 报名记录id
     */
    @ApiModelProperty(value="报名记录id")
    private Long refId;

    /**
     * 金额
     */
    @ApiModelProperty(value="更改后金额")
    private String amount;

    /**
     * 金额
     */
    @ApiModelProperty(value="更改前金额")
    private String beforeAmount;



    /**
     * 备注
     */
    @ApiModelProperty(value="备注")
    private String remark;


    /**
     * 操作时间
     */
    @ApiModelProperty(value="操作时间")
    private Date createTime;

}
