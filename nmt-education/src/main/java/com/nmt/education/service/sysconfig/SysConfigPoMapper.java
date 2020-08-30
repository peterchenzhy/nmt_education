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



    int updateBatch(List<SysConfigPo> list);

    int batchInsert(@Param("list") List<SysConfigPo> list);


    int updateBatchSelective(List<SysConfigPo> list);

    List<SysConfigPo> getAllConfigs();

    SysConfigPo queryByTypeValue(@Param("type") Integer type, @Param("value") Integer value);
}