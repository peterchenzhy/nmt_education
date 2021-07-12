package com.nmt.education.service.course;

import com.nmt.education.pojo.dto.req.CourseSearchDto;
import com.nmt.education.pojo.po.CoursePo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface CoursePoMapper {
    int insertSelective(CoursePo record);

    CoursePo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CoursePo record);

    void invalidByPrimaryKey(@Param("id") Long id, @Param("operator") Integer operator);

    List<CoursePo> queryByDto(@Param("dto") CourseSearchDto dto, @Param("campusList") List<Integer> campusList,
                              @Param("gradeList") List<Integer> gradeList);

    List<CoursePo> queryFuzzy(@Param("name") String name);
}