package com.nmt.education.service.student.account;

import com.nmt.education.pojo.po.StudentAccountFlowPo;

import java.util.List;

import com.nmt.education.pojo.vo.ExpenseDetailFlowVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface StudentAccountFlowPoMapper {
    int insertSelective(StudentAccountFlowPo record);

    StudentAccountFlowPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StudentAccountFlowPo record);

    int updateBatchSelective(List<StudentAccountFlowPo> list);

    int batchInsert(@Param("list") List<StudentAccountFlowPo> list);

  List<ExpenseDetailFlowVo> queryByRegisterId(@Param("registerId") Long registerId);
}