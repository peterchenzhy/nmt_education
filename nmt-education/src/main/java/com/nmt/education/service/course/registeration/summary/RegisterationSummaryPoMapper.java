package com.nmt.education.service.course.registeration.summary;

import com.nmt.education.pojo.dto.req.RegisterSummarySearchDto;
import com.nmt.education.pojo.po.RegisterationSummaryPo;
import com.nmt.education.pojo.vo.RegisterSummaryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    List<RegisterSummaryVo> queryBySearchDto(@Param("dto") RegisterSummarySearchDto dto);
}