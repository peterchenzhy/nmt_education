package com.nmt.education.pojo.vo;

import com.nmt.education.commmons.RoleIdEnum;
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
    @ApiModelProperty(value = "角色id")
    private String roleId;
    @ApiModelProperty(value = "角色名称")
    private String roleIdStr;
    @ApiModelProperty(value = "token")
    private String token;

    public UserVo(UserPo userPo) {
        this.name = userPo.getName();
        this.logInUser = userPo.getCode();
        this.roleId = String.valueOf(userPo.getRoleId());
        this.roleIdStr = RoleIdEnum.code2Display(userPo.getRoleId());
    }

    public UserVo(){

    }
}
