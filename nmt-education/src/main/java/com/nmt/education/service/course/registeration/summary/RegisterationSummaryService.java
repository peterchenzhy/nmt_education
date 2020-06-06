package com.nmt.education.service.course.registeration.summary;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.CourseRegisterReqDto;
import com.nmt.education.pojo.dto.req.RegisterSummarySearchDto;
import com.nmt.education.pojo.po.CourseRegistrationPo;
import com.nmt.education.pojo.po.RegisterationSummaryPo;
import com.nmt.education.pojo.vo.CourseSignInItem;
import com.nmt.education.pojo.vo.RegisterSummaryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class RegisterationSummaryService {

    @Resource
    private RegisterationSummaryPoMapper registerationSummaryPoMapper;


    public RegisterationSummaryPo dto2po(CourseRegisterReqDto dto, int updator, CourseRegistrationPo courseRegistrationPo, Long e) {
        RegisterationSummaryPo registerationSummaryPo = new RegisterationSummaryPo();
        registerationSummaryPo.setStudentId(dto.getStudentId());
        registerationSummaryPo.setCourseId(courseRegistrationPo.getCourseId());
        registerationSummaryPo.setCourseScheduleId(e);
        registerationSummaryPo.setCourseRegistrationId(courseRegistrationPo.getId());
        registerationSummaryPo.setSignIn(Enums.signInType.未签到.getCode());
        registerationSummaryPo.setStatus(StatusEnum.VALID.getCode());
        registerationSummaryPo.setCreator(updator);
        registerationSummaryPo.setCreateTime(new Date());
        registerationSummaryPo.setOperator(updator);
        registerationSummaryPo.setOperateTime(new Date());
        return registerationSummaryPo;
    }

    public int insertSelective(RegisterationSummaryPo record) {
        return registerationSummaryPoMapper.insertSelective(record);
    }


    public RegisterationSummaryPo selectByPrimaryKey(Long id) {
        return registerationSummaryPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(RegisterationSummaryPo record) {
        return registerationSummaryPoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<RegisterationSummaryPo> list) {
        return registerationSummaryPoMapper.updateBatch(list);
    }


    public int updateBatchSelective(List<RegisterationSummaryPo> list) {
        return registerationSummaryPoMapper.updateBatchSelective(list);
    }


    public int batchInsert(List<RegisterationSummaryPo> list) {
        return registerationSummaryPoMapper.batchInsert(list);
    }


    public int insertOrUpdate(RegisterationSummaryPo record) {
        return registerationSummaryPoMapper.insertOrUpdate(record);
    }


    public int insertOrUpdateSelective(RegisterationSummaryPo record) {
        return registerationSummaryPoMapper.insertOrUpdateSelective(record);
    }

    public List<RegisterSummaryVo> queryBySearchDto(RegisterSummarySearchDto dto) {
        return this.registerationSummaryPoMapper.queryBySearchDto(dto);
    }

    /**
     * 根据报名id 查询报名汇总信息
     *
     * @param id
     * @return java.util.List<com.nmt.education.pojo.po.RegisterationSummaryPo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/11 22:50
     */
    public List<RegisterationSummaryPo> queryByRegisterId(Long id) {
        return this.registerationSummaryPoMapper.queryByRegisterId(id);
    }

    /**
     * 根据报名id 查询报名汇总信息page
     *
     * @param id
     * @return java.util.List<com.nmt.education.pojo.po.RegisterationSummaryPo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/11 22:50
     */
    public PageInfo<RegisterationSummaryPo> queryPageByRegisterId(Long id, Integer pageNo, Integer pageSize) {
        return PageHelper.startPage(pageNo, pageSize).doSelectPageInfo(() -> this.registerationSummaryPoMapper.queryByRegisterId(id));
    }

    /**
     * 根据 课程日期id 查询报名记录
     *
     * @param courseScheduleId
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/28 22:45
     */
    public List<CourseSignInItem> queryByCourseScheduleId(Long courseScheduleId) {
        if (Objects.isNull(courseScheduleId)) {
            return Collections.emptyList();
        }
        return this.registerationSummaryPoMapper.queryByCourseScheduleId(courseScheduleId);
    }

    /**
     * 批量签到
     *
     * @param list
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/28 23:09
     */
    public void signIn(List<CourseSignInItem> list, Integer operator) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        this.registerationSummaryPoMapper.signIn(list, operator);
    }

    /**
     * 更新 学生课表 签到状态
     *
     * @param ids       主键id
     * @param logInUser 操作人
     * @param signIn 签到状态
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/6/6 14:05
     */
    public void updateSignIn(List<Long> ids, Integer logInUser, Enums.signInType signIn) {
        if(CollectionUtils.isEmpty(ids)){
            return ;
        }
        this.registerationSummaryPoMapper.updateSignIn(ids,logInUser,signIn.getCode());
    }
}
