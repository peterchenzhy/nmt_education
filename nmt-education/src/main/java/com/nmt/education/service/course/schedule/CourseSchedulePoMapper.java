package com.nmt.education.service.course.schedule;

import com.nmt.education.pojo.po.CourseSchedulePo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
interface CourseSchedulePoMapper {
    int insertSelective(CourseSchedulePo record);

    CourseSchedulePo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseSchedulePo record);

    int updateBatch(List<CourseSchedulePo> list);

    int updateBatchSelective(List<CourseSchedulePo> list);

    int batchInsert(@Param("list") List<CourseSchedulePo> list);

    int insertOrUpdate(CourseSchedulePo record);

    int insertOrUpdateSelective(CourseSchedulePo record);

    List<CourseSchedulePo> queryByCourseId(@Param("id") Long id);

    int signIn(@Param("id") Long id, @Param("operator") Integer operator);

    void invalidByCourseId(@Param("courseId") Long courseId, @Param("operator") Integer operator);

    void changeTeacher(@Param("newTeacherId") Long newTeacherId);
}