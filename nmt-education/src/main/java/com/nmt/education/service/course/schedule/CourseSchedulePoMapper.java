package com.nmt.education.service.course.schedule;

import com.nmt.education.pojo.dto.req.TeacherScheduleReqDto;
import com.nmt.education.pojo.po.CourseSchedulePo;
import com.nmt.education.pojo.vo.TeacherScheduleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
interface CourseSchedulePoMapper {

    CourseSchedulePo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseSchedulePo record);


    int updateBatchSelective(List<CourseSchedulePo> list);

    int batchInsert(@Param("list") List<CourseSchedulePo> list);


    List<CourseSchedulePo> queryByCourseId(@Param("id") Long id);
    List<CourseSchedulePo> queryByCourseIds(@Param("ids") List<Long> ids);

    int signIn(@Param("id") Long id, @Param("operator") Integer operator);

    void invalidByCourseId(@Param("courseId") Long courseId, @Param("operator") Integer operator);

    void changeTeacher(@Param("courseId") Long courseId, @Param("newTeacherId") Long newTeacherId);

    List<CourseSchedulePo> queryByIds(@Param("ids") List<Long> ids);

    List<TeacherScheduleDto> teacherSchedule(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                             @Param("campusList") List<Integer> campusList);

    List<String> getTeacherPay(@Param("dto") TeacherScheduleReqDto dto, @Param("campusList") List<Integer> campusList);
}