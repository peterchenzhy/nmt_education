package com.nmt.education.service.course.registeration;

import com.nmt.education.pojo.po.RegistrationExpenseDetailFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
interface RegistrationExpenseDetailFlowMapper {
    int insertSelective(RegistrationExpenseDetailFlow record);

    RegistrationExpenseDetailFlow selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RegistrationExpenseDetailFlow record);

    int updateBatchSelective(List<RegistrationExpenseDetailFlow> list);

    int batchInsert(@Param("list") List<RegistrationExpenseDetailFlow> list);
}