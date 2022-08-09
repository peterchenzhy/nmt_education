package com.nmt.education.pojo.po;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录流水
 * @TableName user_login_log
 */
@Getter
@Setter
public class UserLoginLogPo implements Serializable {
    private static final long serialVersionUID = 3805063709612053098L;
    /**
     * 主键id
     *
     * @mbg.generated 2022-08-09 21:44:16
     */
    private Long id;

    /**
     * 员工姓名
     *
     * @mbg.generated 2022-08-09 21:44:16
     */
    private String name;

    /**
     * 工号
     *
     * @mbg.generated 2022-08-09 21:44:16
     */
    private Integer code;

    /**
     * 创建人
     *
     * @mbg.generated 2022-08-09 21:44:16
     */
    private Integer creator;

    /**
     * 创建时间
     *
     * @mbg.generated 2022-08-09 21:44:16
     */
    private Date createTime;

    /**
     * 更改人
     *
     * @mbg.generated 2022-08-09 21:44:16
     */
    private Integer operator;

    /**
     * 更改时间
     *
     * @mbg.generated 2022-08-09 21:44:16
     */
    private Date operateTime;

    /**
     * 角色
     *
     * @mbg.generated 2022-08-09 21:44:16
     */
    private Integer roleId;


}