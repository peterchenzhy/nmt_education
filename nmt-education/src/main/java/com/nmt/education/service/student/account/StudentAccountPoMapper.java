package com.nmt.education.service.student.account;

import com.nmt.education.pojo.dto.req.StudentSearchReqDto;
import com.nmt.education.pojo.po.StudentAccountPo;

import java.util.List;

import com.nmt.education.pojo.vo.StudentAccountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.context.annotation.ApplicationScope;

@Mapper
interface StudentAccountPoMapper {
    int insertSelective(StudentAccountPo record);

    StudentAccountPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StudentAccountPo record);

    int updateBatchSelective(List<StudentAccountPo> list);

    int batchInsert(@Param("list") List<StudentAccountPo> list);

    StudentAccountPo querybyUserId(@Param("userId") Long userId);

    void updateByVersion(StudentAccountPo accountPo);

    List<StudentAccountVo> queryAccount(@Param("studentId") Long studentId);
}