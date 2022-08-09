package com.nmt.education.service.user;

import com.nmt.education.pojo.po.UserLoginLogPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Entity generate.UserLoginLog
 */
@Mapper
 interface UserLoginLogMapper {


    /**
     *
     * @mbg.generated 2022-08-09 21:44:16
     */
    int insertSelective(UserLoginLogPo record);



    /**
     *
     * @mbg.generated 2022-08-09 21:44:16
     */
    int updateByPrimaryKeySelective(UserLoginLogPo record);


}