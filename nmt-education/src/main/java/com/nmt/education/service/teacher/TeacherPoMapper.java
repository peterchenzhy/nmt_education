package com.nmt.education.service.teacher;

import com.nmt.education.pojo.po.TeacherPo;
import com.nmt.education.pojo.vo.StudentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherPoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TeacherPo record);

    int insertSelective(TeacherPo record);

    TeacherPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TeacherPo record);

    int updateByPrimaryKey(TeacherPo record);

    List<StudentVo> queryFuzzy(@Param("name") String name);
}