CREATE TABLE `grade_authorization` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                       `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '名称',
                                       `code` int(8) NOT NULL COMMENT 'code',
                                       `user_id` int(10) NOT NULL DEFAULT '-1' COMMENT '工号',
                                       `remark` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                       `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '有效：1 无效：0',
                                       `creator` int(8) NOT NULL DEFAULT '0' COMMENT '创建人',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `operator` int(8) NOT NULL DEFAULT '0' COMMENT '更改人',
                                       `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更改时间',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='年级授权';