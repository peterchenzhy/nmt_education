package com.nmt.education.service.course.expense;

import com.nmt.education.pojo.po.CourseExpensePo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface CourseExpensePoMapper {
    int insertSelective(CourseExpensePo record);

    CourseExpensePo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseExpensePo record);

    int updateBatch(List<CourseExpensePo> list);

    int updateBatchSelective(List<CourseExpensePo> list);

    int batchInsert(@Param("list") List<CourseExpensePo> list);

    int insertOrUpdate(CourseExpensePo record);

    int insertOrUpdateSelective(CourseExpensePo record);

    List<CourseExpensePo> queryByCourseId(@Param("id") Long id);

    void invalidByCourseId(@Param("courseId") Long courseId, @Param("operator") Integer operator);
}