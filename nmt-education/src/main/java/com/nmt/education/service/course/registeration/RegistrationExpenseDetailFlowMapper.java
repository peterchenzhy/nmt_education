package com.nmt.education.service.course.registeration;

import com.nmt.education.pojo.po.RegistrationExpenseDetailFlowPo;
import com.nmt.education.pojo.vo.FeeStatisticsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
interface RegistrationExpenseDetailFlowMapper {
    int insertSelective(RegistrationExpenseDetailFlowPo record);

    RegistrationExpenseDetailFlowPo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RegistrationExpenseDetailFlowPo record);

    int updateBatchSelective(List<RegistrationExpenseDetailFlowPo> list);

    int batchInsert(@Param("list") List<RegistrationExpenseDetailFlowPo> list);

    List<RegistrationExpenseDetailFlowPo> queryByRegisterId(@Param("registerId") Long registerId);

    List<FeeStatisticsVo> feeStatistics(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                        @Param("campusList") List<Integer> campusList, @Param("typeList") List<Integer> typeList);
}