package com.nmt.education.service.course.registeration;

import com.nmt.education.pojo.dto.req.RegisterSearchReqDto;
import com.nmt.education.pojo.po.CourseRegisterCount;
import com.nmt.education.pojo.po.CourseRegistrationPo;

import java.util.Date;
import java.util.List;

import com.nmt.education.pojo.vo.CourseRegistrationListVo;
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

    List<CourseRegistrationPo> queryByDto(@Param("dto") RegisterSearchReqDto dto, @Param("campusList") List<Integer> campusList);

    CourseRegistrationVo queryVoById(@Param("id") Long id);

    CourseRegistrationListVo queryByCourseStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    List<CourseRegistrationPo> queryByCourseId(@Param("courseId") Long courseId, @Param("registerStatus") Integer registerStatus);

    List<CourseRegisterCount> countStudentByCourse(@Param("courseIds") List<Long> courseIds);

    long registerStudentSummaryTotal(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                     @Param("year") Integer year, @Param("season") Integer season,
                                     @Param("userCode") Integer userCode,
                                     @Param("campusList") List<Integer> campusList);
}