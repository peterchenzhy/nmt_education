package com.nmt.education.commmons;

/**
 * @author yemingxing
 * @summary 枚举定义 interface
 * @Copyright (c) 2019, Ke Group All Rights Reserved.
 * @since 2019-07-05 17:26
 */
public interface IEnum {

    /**
     * 获取 枚举编码
     *
     * @return 枚举编码
     */
    Object getCode();

    /**
     * 获取 枚举描述
     *
     * @return 枚举描述
     */
    String getDesc();
}
