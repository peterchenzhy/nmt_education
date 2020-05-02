package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.UserPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserVo {

    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "logInUser")
    private Integer logInUser;
    @ApiModelProperty(value = "roleId")
    private String roleId;

    public UserVo(UserPo userPo) {
        this.name = userPo.getName();
        this.logInUser = userPo.getCode();
        // TODO: 2020/5/2  roleId 暂时用工号代替
        this.roleId = String.valueOf(userPo.getCode());
    }
}
