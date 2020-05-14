package com.nmt.education.service.student;

import com.nmt.education.pojo.po.StudentPo;
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

    List<StudentPo> queryFuzzy(@Param("name") String name);

    List<StudentPo> query(@Param("phone") String phone);

    void invalidByPrimaryKey(@Param("operator") Integer operator, @Param("id") Long id);

    List<StudentPo> queryByIds(@Param("ids") List<Long> ids);
}