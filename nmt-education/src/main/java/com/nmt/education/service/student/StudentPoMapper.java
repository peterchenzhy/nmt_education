package com.nmt.education.service.student;

import com.nmt.education.pojo.po.StudentPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentPoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StudentPo record);

    int insertSelective(StudentPo record);

    StudentPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StudentPo record);

    int updateByPrimaryKey(StudentPo record);
}