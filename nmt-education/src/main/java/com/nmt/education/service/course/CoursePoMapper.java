package com.nmt.education.service.course;

import com.nmt.education.pojo.po.CoursePo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CoursePoMapper {
    int insertSelective(CoursePo record);

    CoursePo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CoursePo record);

    int updateBatch(List<CoursePo> list);

    int updateBatchSelective(List<CoursePo> list);

    int batchInsert(@Param("list") List<CoursePo> list);

    int insertOrUpdate(CoursePo record);

    int insertOrUpdateSelective(CoursePo record);

    void invalidByPrimaryKey(@Param("id") Long id, @Param("operator") Integer operator);
}