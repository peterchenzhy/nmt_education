package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class TeacherReqDto {


    @ApiModelProperty(value = "数据id,仅编辑使用")
    private Long id;
    @ApiModelProperty(value = "数据是否删除标志，默认false")
    private Boolean deleteFlg = false;
    /**
     * 姓名
     */
    @NotNull(message = "姓名不能为空")
    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    /**
     * 生日
     */
    @ApiModelProperty(value = "生日", dataType = "Date")
    private Date birthday;

    /**
     * 学校
     */
    @ApiModelProperty(value = "学校")
    private String school;

    /**
     * 电话
     */
    @ApiModelProperty(value = "手机号", required = true)
    @Size(max = 11, message = "手机号不能超过11位")
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

    @ApiModelProperty(value = "教师薪资配置信息")
    private List<TeacherSalaryConfigReqDto> teacherSalaryConfigList;
}