package com.nmt.education.service.course.registeration.summary;

import com.nmt.education.pojo.po.RegisterationSummaryPo;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
  interface RegisterationSummaryPoMapper {
    int insertSelective(RegisterationSummaryPo record);

    RegisterationSummaryPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RegisterationSummaryPo record);

    int updateBatch(List<RegisterationSummaryPo> list);

    int updateBatchSelective(List<RegisterationSummaryPo> list);

    int batchInsert(@Param("list") List<RegisterationSummaryPo> list);

    int insertOrUpdate(RegisterationSummaryPo record);

    int insertOrUpdateSelective(RegisterationSummaryPo record);
}