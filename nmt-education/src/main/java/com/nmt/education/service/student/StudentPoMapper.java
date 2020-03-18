package com.nmt.education.service.student;

import com.nmt.education.pojo.po.StudentPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Mapper
public interface StudentPoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StudentPo record);

    int insertSelective(StudentPo record);

    StudentPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StudentPo record);

    int updateByPrimaryKey(StudentPo record);
}