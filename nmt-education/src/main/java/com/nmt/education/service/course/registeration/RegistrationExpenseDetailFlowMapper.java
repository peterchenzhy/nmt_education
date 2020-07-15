package com.nmt.education.service.course.registeration;

import com.nmt.education.pojo.po.RegistrationExpenseDetailFlowPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
interface RegistrationExpenseDetailFlowMapper {
    int insertSelective(RegistrationExpenseDetailFlowPo record);

    RegistrationExpenseDetailFlowPo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RegistrationExpenseDetailFlowPo record);

    int updateBatchSelective(List<RegistrationExpenseDetailFlowPo> list);

    int batchInsert(@Param("list") List<RegistrationExpenseDetailFlowPo> list);

    List<RegistrationExpenseDetailFlowPo> queryByRegisterId(@Param("registerId") Long registerId);
}