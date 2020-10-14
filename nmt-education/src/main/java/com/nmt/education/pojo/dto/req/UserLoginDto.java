package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class UserLoginDto {

    /**
     * 用户code或手机号
     */
    @NotNull(message = "用户code或者手机号不能为空")
    @ApiModelProperty(value = "用户code或手机号", required = true)
    private String code;

    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "登录密码", required = true)
    private String password;

}