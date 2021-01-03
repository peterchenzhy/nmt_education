package com.nmt.education.service.course.registeration;

import com.nmt.education.pojo.po.RegistrationExpenseDetailFlowPo;
import com.nmt.education.pojo.vo.FeeStatisticsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
interface RegistrationExpenseDetailFlowMapper {

    RegistrationExpenseDetailFlowPo selectByPrimaryKey(Integer id);

    int batchInsert(@Param("list") List<RegistrationExpenseDetailFlowPo> list);

    List<RegistrationExpenseDetailFlowPo> queryByRegisterId(@Param("registerId") Long registerId);

    List<FeeStatisticsVo> feeStatistics(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                        @Param("year") Integer year, @Param("season") Integer season,
                                        @Param("campusList") List<Integer> campusList,
                                        @Param("userCode") Integer userCode , @Param("typeList") List<Integer> typeList
                                      );

    List<String> flowSummary(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                             @Param("year") Integer year, @Param("season") Integer season,
                             @Param("campusList") List<Integer> campusList,
                             @Param("userCode") Integer userCode,
                             @Param("type") List<Integer> type);
}