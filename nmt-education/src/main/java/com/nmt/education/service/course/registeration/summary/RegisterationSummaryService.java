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
import java.util.*;

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
        registerationSummaryPo.setSignIn(Enums.SignInType.未签到.getCode());
        registerationSummaryPo.setStatus(StatusEnum.VALID.getCode());
        registerationSummaryPo.setCreator(updator);
        registerationSummaryPo.setCreateTime(new Date());
        registerationSummaryPo.setOperator(updator);
        registerationSummaryPo.setOperateTime(new Date());
        return registerationSummaryPo;
    }



    public RegisterationSummaryPo selectByPrimaryKey(Long id) {
        return registerationSummaryPoMapper.selectByPrimaryKey(id);
    }


    public int batchInsert(List<RegisterationSummaryPo> list) {
        if(CollectionUtils.isEmpty(list)){
            return 0 ;
        }
        return registerationSummaryPoMapper.batchInsert(list);
    }



    public List<RegisterSummaryVo> queryBySearchDto(RegisterSummarySearchDto dto, List<Integer> campusList, List<Integer> gradeList) {
        return this.registerationSummaryPoMapper.queryBySearchDto(dto,campusList,gradeList);
    }

    public int queryCountBySearchDto(RegisterSummarySearchDto dto, List<Integer> campusList,Integer signInStatus,List<Integer> gradeList) {
        return this.registerationSummaryPoMapper.queryCountBySearchDto(dto,campusList,signInStatus,gradeList);
    }

    /**
     * 根据报名id 查询报名汇总信息 会包含退费的课程日历
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

    public List<RegisterationSummaryPo> queryByRegisterIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return Collections.EMPTY_LIST;
        }
        return this.registerationSummaryPoMapper.queryByRegisterIds(ids);
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
     * @param signIn    签到状态
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/6/6 14:05
     */
    public void updateSignIn(List<Long> ids, Integer logInUser, Enums.SignInType signIn,String remark) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        this.registerationSummaryPoMapper.updateSignIn(ids, logInUser, signIn.getCode(),  remark);
    }

    public List<RegisterationSummaryPo> queryByCourseId(Long courseId, List<Integer> signList) {
        return this.registerationSummaryPoMapper.queryByCourseId(courseId,signList);
    }

    public int checkSignIn(long courseScheduleId) {
        return this.registerationSummaryPoMapper.checkSignIn(courseScheduleId);
    }

    public long countRegistration(RegisterSummarySearchDto dto, List<Integer> campusList) {
        return this.registerationSummaryPoMapper.countRegistration(dto,campusList);
    }
}
