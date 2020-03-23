package com.nmt.education.pojo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnumVo {

    /**
     * 显示值
     */
    private String label;
    /**
     * 实际值
     */
    private Integer value;

    /**
     * 是否是dbConfig
     */
    private boolean dbConfig;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 类型code
     */
    private String typeCode;

    /**
     * 类型描述
     */
    private String typeDesc;

}
