package com.nmt.education.service.sysconfig;

import com.nmt.education.pojo.po.SysConfigPo;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysConfigPoMapper {
    int insertSelective(SysConfigPo record);

    SysConfigPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysConfigPo record);

    int updateByPrimaryKey(SysConfigPo record);

    int updateBatch(List<SysConfigPo> list);

    int batchInsert(@Param("list") List<SysConfigPo> list);

    int insertOrUpdate(SysConfigPo record);

    int insertOrUpdateSelective(SysConfigPo record);

    int updateBatchSelective(List<SysConfigPo> list);
}