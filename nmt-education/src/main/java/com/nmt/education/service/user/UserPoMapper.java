package com.nmt.education.service.user;

import com.nmt.education.pojo.dto.req.UserLoginDto;
import com.nmt.education.pojo.po.UserPo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface UserPoMapper {
    int insertSelective(UserPo record);

    UserPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserPo record);

    int updateBatch(List<UserPo> list);

    int updateBatchSelective(List<UserPo> list);

    int batchInsert(@Param("list") List<UserPo> list);

    int insertOrUpdate(UserPo record);

    int insertOrUpdateSelective(UserPo record);

    UserPo queryByLoginDto(@Param("dto") UserLoginDto dto);
}