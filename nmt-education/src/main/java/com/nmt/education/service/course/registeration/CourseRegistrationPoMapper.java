package com.nmt.education.service.course.registeration;

import com.nmt.education.pojo.dto.req.RegisterSearchReqDto;
import com.nmt.education.pojo.po.CourseRegistrationPo;

import java.util.List;

import com.nmt.education.pojo.vo.CourseRegistrationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface CourseRegistrationPoMapper {
    int insertSelective(CourseRegistrationPo record);

    CourseRegistrationPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseRegistrationPo record);

    int updateBatch(List<CourseRegistrationPo> list);

    int updateBatchSelective(List<CourseRegistrationPo> list);

    int batchInsert(@Param("list") List<CourseRegistrationPo> list);

    int insertOrUpdate(CourseRegistrationPo record);

    int insertOrUpdateSelective(CourseRegistrationPo record);

    List<CourseRegistrationPo> queryByDto(@Param("dto") RegisterSearchReqDto dto);

    CourseRegistrationVo queryVoById(@Param("id") Long id);

    CourseRegistrationPo queryByCourseStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}