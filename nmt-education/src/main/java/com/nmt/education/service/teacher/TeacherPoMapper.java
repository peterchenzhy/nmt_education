package com.nmt.education.service.teacher;

import com.nmt.education.pojo.po.TeacherPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeacherPoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TeacherPo record);

    int insertSelective(TeacherPo record);

    TeacherPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TeacherPo record);

    int updateByPrimaryKey(TeacherPo record);
}