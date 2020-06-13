package com.nmt.education.service.course.registeration;

import com.nmt.education.pojo.po.RegistrationExpenseDetailPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
interface RegistrationExpenseDetailPoMapper {
    int insertSelective(RegistrationExpenseDetailPo record);

    RegistrationExpenseDetailPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RegistrationExpenseDetailPo record);

    int updateBatch(List<RegistrationExpenseDetailPo> list);

    int batchInsert(@Param("list") List<RegistrationExpenseDetailPo> list);

    List<RegistrationExpenseDetailPo> queryRegisterId(@Param("registerId") Long registerId);

    List<RegistrationExpenseDetailPo> selectByIds(@Param("ids") List<Long> ids);
}