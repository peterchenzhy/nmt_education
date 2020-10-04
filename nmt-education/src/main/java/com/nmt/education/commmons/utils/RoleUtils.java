package com.nmt.education.commmons.utils;

import com.nmt.education.commmons.RoleIdEnum;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.nmt.education.commmons.Consts.ROLE_ID_HEAD;

public class RoleUtils {

    /**
     * 校验角色
     * @param roleId
     */
    public static void check校长财务(@RequestHeader(ROLE_ID_HEAD) String roleId) {
        Assert.notNull(roleId,"角色不存在");
        Assert.isTrue(Integer.valueOf(roleId).intValue() == RoleIdEnum.校长.getCode()
                || Integer.valueOf(roleId).intValue() == RoleIdEnum.财务.getCode(), "您没有该功能权限");
    }

    /**
     * 校验角色
     * @param roleId
     */
    public static void check校长(@RequestHeader(ROLE_ID_HEAD) String roleId) {
        Assert.notNull(roleId,"角色不存在");
        Assert.isTrue(Integer.valueOf(roleId).intValue() == RoleIdEnum.校长.getCode(), "您没有该功能权限");
    }
}
