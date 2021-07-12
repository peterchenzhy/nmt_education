package com.nmt.education.service.authorization.grade;

import com.nmt.education.pojo.po.CampusAuthorizationPo;
import com.nmt.education.pojo.po.GradeAuthorizationPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity generate.GradeAuthorization
 */
@Mapper
interface GradeAuthorizationPoMapper {
    /**
     * @mbg.generated 2021-06-11 16:04:11
     */
    int deleteByPrimaryKey(Long id);

    /**
     * @mbg.generated 2021-06-11 16:04:11
     */
    int insert(GradeAuthorizationPo record);

    /**
     * @mbg.generated 2021-06-11 16:04:11
     */
    int insertSelective(GradeAuthorizationPo record);

    /**
     * @mbg.generated 2021-06-11 16:04:11
     */
    GradeAuthorizationPo selectByPrimaryKey(Long id);

    /**
     * @mbg.generated 2021-06-11 16:04:11
     */
    int updateByPrimaryKeySelective(GradeAuthorizationPo record);

    /**
     * @mbg.generated 2021-06-11 16:04:11
     */
    int updateByPrimaryKey(GradeAuthorizationPo record);

   List<CampusAuthorizationPo> getGradeAuthorizationByUserId(@Param("userId") Integer userId);
}