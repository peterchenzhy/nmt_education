package com.nmt.education.service.user;

import com.nmt.education.pojo.dto.req.UserLoginDto;
import com.nmt.education.pojo.po.UserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface UserPoMapper {
    int insertSelective(UserPo record);

    UserPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserPo record);

    UserPo queryByLoginDto(@Param("dto") UserLoginDto dto);
}