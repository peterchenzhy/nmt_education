package com.nmt.education.service.course.registeration;

import com.nmt.education.pojo.po.RegistrationExpenseDetailPo;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
  interface RegistrationExpenseDetailPoMapper {
    int insertSelective(RegistrationExpenseDetailPo record);

    RegistrationExpenseDetailPo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RegistrationExpenseDetailPo record);

    int updateBatch(List<RegistrationExpenseDetailPo> list);

    int updateBatchSelective(List<RegistrationExpenseDetailPo> list);

    int batchInsert(@Param("list") List<RegistrationExpenseDetailPo> list);

    int insertOrUpdate(RegistrationExpenseDetailPo record);

    int insertOrUpdateSelective(RegistrationExpenseDetailPo record);
}