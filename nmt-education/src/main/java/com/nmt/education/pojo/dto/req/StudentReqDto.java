package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class StudentReqDto {

    @ApiModelProperty(value = "数据id,仅编辑使用")
    private Long id;

    @ApiModelProperty(value = "编辑标志；0：无变化，1：新增，2：编辑，3：需要删除")
    private Integer editFlag = 0;
    /**
     * 学生姓名
     */
    @NotNull(message = "学生姓名不能为空")
    @ApiModelProperty(value = "学生姓名", required = true)
    private String name;

    /**
     * 生日
     */
    @ApiModelProperty(value = "生日", dataType = "Date")
    private Date birthday;

    /**
     * 学校
     */
    @ApiModelProperty(value = "学校", required = true)
    private String school;

    /**
     * 年级 小学初中1-9 高中10-12
     */
    @NotNull(message = "年级不能为空")
    @Range(min = 1, message = "年级必须大于0")
    @ApiModelProperty(value = "年级", required = true)
    private Integer grade;
    /**
     * 所在校区
     */
    @NotNull(message = "所在校区不能为空")
    @ApiModelProperty(value = "所在校区", required = true)
    @Range(min = 1, message = "所在校区必须大于0")
    private Integer campus;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话", required = true)
    private String phone;
    /**
     * 性别
     */
    @ApiModelProperty(value = "性别", required = true)
    @Range(min = 0, max = 1, message = "性别只能是0或1")
    private Integer sex;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;


}