package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "com-nmt-education-pojo-po-StudentPo")
@Getter
@Setter
@ToString
public class StudentPo {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 学生编码
     */
    @ApiModelProperty(value = "学生编码")
    private String studentCode;

    /**
     * 学生姓名
     */
    @ApiModelProperty(value = "学生姓名")
    private String name;

    /**
     * 生日
     */
    @ApiModelProperty(value = "生日")
    private Date birthday;

    /**
     * 学校
     */
    @ApiModelProperty(value = "学校")
    private String school;

    /**
     * 年级 小学初中1-9 高中10-12
     */
    @ApiModelProperty(value = "年级 小学初中1-9 高中10-12 ")
    private Integer grade;

    /**
     * 联系人1电话
     */
    @ApiModelProperty(value = "联系人1电话,")
    private String phone;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private Integer sex;

    /**
     * 所在校区
     */
    @ApiModelProperty(value = "所在校区")
    private Integer campus;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 有效：1 无效：0
     */
    @ApiModelProperty(value = "有效：1 无效：0")
    private Integer status;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private Integer creator;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更改人
     */
    @ApiModelProperty(value = "更改人")
    private Integer operator;

    /**
     * 更改时间
     */
    @ApiModelProperty(value = "更改时间")
    private Date operateTime;
}