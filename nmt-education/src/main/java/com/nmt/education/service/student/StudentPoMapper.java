package com.nmt.education.service.student;

import com.nmt.education.pojo.po.StudentPo;
import com.nmt.education.pojo.vo.StudentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentPoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StudentPo record);

    int insertSelective(StudentPo record);

    StudentPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StudentPo record);

    int updateByPrimaryKey(StudentPo record);

    StudentPo queryLike(@Param("name") String name);

    List<StudentVo> queryFuzzy(@Param("name") String name);
}