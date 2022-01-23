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


    int batchInsert(@Param("list") List<StudentAccountFlowPo> list);

    List<StudentAccountFlowPo> queryByRegisterIds(@Param("registerIds") List<Long> registerIds);

    List<StudentAccountFlowPo> queryByAccountId(@Param("accountId") Long accountId);
}