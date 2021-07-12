package com.nmt.education.service.authorization.campus;

import com.nmt.education.pojo.po.CampusAuthorizationPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
interface CampusAuthorizationPoMapper {
    int insertSelective(CampusAuthorizationPo record);

    CampusAuthorizationPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CampusAuthorizationPo record);

    int updateBatchSelective(List<CampusAuthorizationPo> list);

    int batchInsert(@Param("list") List<CampusAuthorizationPo> list);

    List<CampusAuthorizationPo> getCampusAuthorizationByUserId(@Param("userId") Integer userId);
}