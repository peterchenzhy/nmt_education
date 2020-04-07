package com.nmt.education.service.teacher.config;

import com.nmt.education.pojo.po.TeacherSalaryConfigPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface TeacherSalaryConfigPoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TeacherSalaryConfigPo record);

    int insertSelective(TeacherSalaryConfigPo record);

    TeacherSalaryConfigPo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TeacherSalaryConfigPo record);

    int updateByPrimaryKey(TeacherSalaryConfigPo record);

    void batchInsert(@Param("list") List<TeacherSalaryConfigPo> salaryConfigPoList);

    void invalidBatchByids(@Param("ids") List<Long> ids, @Param("operator") Integer operator, @Param("date") Date date);
}