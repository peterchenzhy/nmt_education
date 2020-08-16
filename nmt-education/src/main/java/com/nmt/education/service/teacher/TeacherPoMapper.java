package com.nmt.education.service.teacher;

import com.nmt.education.pojo.po.TeacherPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherPoMapper {
    int invalidByPrimaryKey(@Param("id") Long id, @Param("operator") Integer operator);

    int insertSelective(TeacherPo record);

    TeacherPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TeacherPo record);

    List<TeacherPo> queryFuzzy(@Param("name") String name);

    List<TeacherPo> query(@Param("phone") String phone);
}