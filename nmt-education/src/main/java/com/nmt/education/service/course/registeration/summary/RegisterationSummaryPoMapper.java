package com.nmt.education.service.course.registeration.summary;

import com.nmt.education.pojo.dto.req.RegisterSummarySearchDto;
import com.nmt.education.pojo.po.RegisterationSummaryPo;
import com.nmt.education.pojo.vo.CourseSignInItem;
import com.nmt.education.pojo.vo.RegisterSummaryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
interface RegisterationSummaryPoMapper {
    int insertSelective(RegisterationSummaryPo record);

    RegisterationSummaryPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RegisterationSummaryPo record);


    int batchInsert(@Param("list") List<RegisterationSummaryPo> list);

    List<RegisterSummaryVo> queryBySearchDto(@Param("dto") RegisterSummarySearchDto dto, @Param("campusList") List<Integer> campusList);

    List<RegisterationSummaryPo> queryByRegisterId(@Param("registerId") Long registerId);

    List<RegisterationSummaryPo> queryByRegisterIds(@Param("ids") List<Long> ids);


    List<CourseSignInItem> queryByCourseScheduleId(@Param("courseScheduleId") Long courseScheduleId);

    void signIn(@Param("list") List<CourseSignInItem> list, @Param("operator") Integer operator);

    void updateSignIn(@Param("ids") List<Long> ids, @Param("logInUser") Integer logInUser, @Param("code") Integer code, @Param("remark") String remark);

    List<RegisterationSummaryPo> selectByIds(@Param("ids") List<Long> ids);

    List<RegisterationSummaryPo> queryByCourseId(@Param("courseId") Long courseId, @Param("signList") List<Integer> signList);

    int queryCountBySearchDto(@Param("dto") RegisterSummarySearchDto dto, @Param("campusList") List<Integer> campusList, @Param("signInStatus") Integer signInStatus);

    int checkSignIn(@Param("courseScheduleId") long courseScheduleId);

    long countRegistration(@Param("dto") RegisterSummarySearchDto dto, @Param("campusList") List<Integer> campusList);
}
