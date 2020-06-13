package com.nmt.education.service.course.schedule;

import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.ExpenseDetailFlowTypeEnum;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.CourseScheduleReqDto;
import com.nmt.education.pojo.po.*;
import com.nmt.education.pojo.vo.CourseSignInItem;
import com.nmt.education.pojo.vo.CourseSignInVo;
import com.nmt.education.service.course.CourseService;
import com.nmt.education.service.course.registeration.CourseRegistrationService;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailService;
import com.nmt.education.service.course.registeration.summary.RegisterationSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class CourseScheduleService {

    @Resource
    private CourseSchedulePoMapper courseSchedulePoMapper;
    @Autowired
    @Lazy
    private CourseService courseService;
    @Autowired
    private RegisterationSummaryService registerationSummaryService;
    @Autowired
    private CourseRegistrationService courseRegistrationService;
    @Autowired
    private RegistrationExpenseDetailService registrationExpenseDetailService;

    public boolean manager(List<CourseScheduleReqDto> dtoList, Long courseId, Integer operator) {
        if (CollectionUtils.isEmpty(dtoList)) {
            invalidByCourseId(courseId, operator);
            return true;
        }
        dtoList.stream().filter(e -> !Enums.SignInType.已签到.getCode().equals(e.getSignIn())).forEach(e -> manager(e, courseId, operator));
        return true;
    }

    private void invalidByCourseId(Long courseId, Integer operator) {
        this.courseSchedulePoMapper.invalidByCourseId(courseId, operator);
    }

    private void manager(CourseScheduleReqDto dto, Long courseId, Integer operator) {
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        List<CourseSchedulePo> addList = new ArrayList<>();
        List<CourseSchedulePo> editList = new ArrayList<>();
        switch (editFlag) {
            case 新增:
                addList.add(newSchedule(operator, dto, courseId));
                break;
            case 修改:
                editList.add(editSchedule(operator, dto));
                break;
            case 需要删除:
                editList.add(delSchedule(operator, dto));
                break;
            case 无变化:
                break;
            default:
                log.error("courseSchedule请求数据不合规，无法辨认editFlag！" + dto);
                break;
        }
        updateBatchSelective(editList);
        batchInsert(addList);

    }

    private CourseSchedulePo delSchedule(Integer operator, CourseScheduleReqDto dto) {
        CourseSchedulePo po = checkExist(dto);
        po.setStatus(StatusEnum.INVALID.getCode());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }

    private CourseSchedulePo editSchedule(Integer operator, CourseScheduleReqDto dto) {
        CourseSchedulePo po = checkExist(dto);
        BeanUtils.copyProperties(dto, po);
        po.setStatus(StatusEnum.VALID.getCode());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }

    private CourseSchedulePo checkExist(CourseScheduleReqDto dto) {
        Assert.notNull(dto.getId(), "编辑课程信息缺少id");
        CourseSchedulePo po = selectByPrimaryKey(dto.getId());
        Assert.notNull(po, "课程信息不存在" + dto.getId());
        Assert.isTrue(po.getSignIn() == 0, "课表已经签到，无法进行修改或者删除,id:" + dto.getId());
        return po;
    }

    /**
     * 新增
     *
     * @param operator
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/24 10:54
     */
    private CourseSchedulePo newSchedule(Integer operator, CourseScheduleReqDto dto, Long courseId) {
        CourseSchedulePo po = new CourseSchedulePo();
        BeanUtils.copyProperties(dto, po);
        po.setCourseId(courseId);
        po.setStatus(StatusEnum.VALID.getCode());
        po.setCreator(operator);
        po.setCreateTime(new Date());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }


    public int insertSelective(CourseSchedulePo record) {
        return courseSchedulePoMapper.insertSelective(record);
    }


    public CourseSchedulePo selectByPrimaryKey(Long id) {
        return courseSchedulePoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(CourseSchedulePo record) {
        return courseSchedulePoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<CourseSchedulePo> list) {
        return courseSchedulePoMapper.updateBatch(list);
    }


    public void updateBatchSelective(List<CourseSchedulePo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        courseSchedulePoMapper.updateBatchSelective(list);
    }


    public void batchInsert(List<CourseSchedulePo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.stream().forEach(e -> {
            if (Objects.isNull(e.getTeacherId())) {
                e.setTeacherId(Consts.DEFAULT_LONG);
            }
        });
        courseSchedulePoMapper.batchInsert(list);
    }


    public int insertOrUpdate(CourseSchedulePo record) {
        return courseSchedulePoMapper.insertOrUpdate(record);
    }


    public int insertOrUpdateSelective(CourseSchedulePo record) {
        return courseSchedulePoMapper.insertOrUpdateSelective(record);
    }

    public List<CourseSchedulePo> queryByCourseId(Long id) {
        return this.courseSchedulePoMapper.queryByCourseId(id);
    }

    /**
     * 签到
     *
     * @param list
     * @param operator
     */
    public void signIn(List<CourseSignInItem> list, Integer operator) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        long courseId = list.get(0).getCourseId();
        CourseSchedulePo po = selectByPrimaryKey(courseId);
        Assert.notNull(po, "课表信息为空，id：" + courseId);
        List<RegistrationExpenseDetailFlow> flowList = new ArrayList<>(list.size());
        List<CourseRegistrationPo> courseRegistrationPoList = new ArrayList<>(list.size());
        List<CourseSignInItem> needUpdate = new ArrayList<>(list.size());
        //消耗记录
        for (CourseSignInItem courseSignInItem : list) {
            RegisterationSummaryPo registerationSummaryPo = registerationSummaryService.selectByPrimaryKey(courseSignInItem.getRegisterSummaryId());
            Assert.isTrue(Objects.nonNull(registerationSummaryPo), "报名课时不存在，id：" + courseSignInItem.getRegisterSummaryId());
            Enums.SignInType target = Enums.SignInType.codeOf(courseSignInItem.getSignIn());
            Enums.SignInType source = Enums.SignInType.codeOf(registerationSummaryPo.getSignIn());
            Boolean isConsumed = Enums.SignInType.isConsumed(source, target);
            if (Objects.isNull(isConsumed)) {
                //状态没有变化
                continue;
            }
            if (!courseSignInItem.getSignIn().equals(registerationSummaryPo.getSignIn())) {
                CourseRegistrationPo courseRegistrationPo =
                        courseRegistrationService.selectByPrimaryKey(registerationSummaryPo.getCourseRegistrationId());
                BigDecimal balanceAmount = new BigDecimal(courseRegistrationPo.getBalanceAmount());
                RegistrationExpenseDetailPo expenseDetailPo = registrationExpenseDetailService.queryRegisterId(courseRegistrationPo.getId())
                        .stream().filter(p -> Consts.普通单节费用.equals(p.getFeeType()) && Enums.FeeDirection.支付.getCode().equals(p.getFeeDirection()) &&
                                Enums.FeeStatus.已缴费.getCode().equals(p.getFeeStatus())).findFirst().get();
                Assert.isTrue(Objects.nonNull(expenseDetailPo), "可退费的记录不存在，id：" + courseSignInItem.getRegisterSummaryId());
                BigDecimal perAmount = new BigDecimal(expenseDetailPo.getPerAmount());
                //设置余额
                if (isConsumed) {
                    courseRegistrationPo.setBalanceAmount(balanceAmount.subtract(perAmount).toPlainString());
                    flowList.add(generateFlow(operator, courseRegistrationPo.getId(),expenseDetailPo.getId(), perAmount, ExpenseDetailFlowTypeEnum.消耗));
                } else {
                    courseRegistrationPo.setBalanceAmount(balanceAmount.add(perAmount).toPlainString());
                    flowList.add(generateFlow(operator, courseRegistrationPo.getId(),expenseDetailPo.getId(), perAmount, ExpenseDetailFlowTypeEnum.还原));
                }
                courseRegistrationPoList.add(courseRegistrationPo);
                needUpdate.add(courseSignInItem);
            }
        }
        //更新课程日历信息
        CourseSchedulePo courseSchedulePo = selectByPrimaryKey(list.get(0).getCourseScheduleId());
        if (list.stream().anyMatch(e -> Enums.SignInType.已签到.getCode().equals(e.getSignIn()))) {
            if (!Enums.SignInType.已签到.getCode().equals(courseSchedulePo.getSignIn())) {
                courseSchedulePo.setSignIn(Enums.SignInType.已签到.getCode());
                courseSchedulePo.setOperator(operator);
                courseSchedulePo.setOperateTime(new Date());
                updateByPrimaryKeySelective(courseSchedulePo);
            }
        } else {
            if (Enums.SignInType.已签到.getCode().equals(courseSchedulePo.getSignIn())) {
                courseSchedulePo.setSignIn(Enums.SignInType.未签到.getCode());
                courseSchedulePo.setOperator(operator);
                courseSchedulePo.setOperateTime(new Date());
                updateByPrimaryKeySelective(courseSchedulePo);
            }
        }
        //更新状态
        if (!CollectionUtils.isEmpty(needUpdate)) {
            registerationSummaryService.signIn(needUpdate, operator);
            //更新报名记录
            courseRegistrationService.updateBatch(courseRegistrationPoList);
            //插入流水
            registrationExpenseDetailService.batchInsertFlow(flowList);
        }else{
            log.info("课程签到没有状态变更，"+list);
        }

    }

    /**
     * 生成流水
     *
     * @param operator
     * @param courseRegistrationId
     * @param perAmount
     * @param type
     * @return com.nmt.education.pojo.po.RegistrationExpenseDetailFlow
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/6/13 14:45
     */
    private RegistrationExpenseDetailFlow generateFlow(Integer operator, Long courseRegistrationId, Long courseRegistrationExpenseId,
                                                       BigDecimal perAmount,
                                                       ExpenseDetailFlowTypeEnum type) {
        RegistrationExpenseDetailFlow flow = new RegistrationExpenseDetailFlow();
        flow.setRegistrationId(courseRegistrationId);
        flow.setFeeType(Consts.普通单节费用);
        flow.setType(type.getCode());
        flow.setAmount(perAmount.toPlainString());
        flow.setRegisterExpenseDetailId(courseRegistrationExpenseId);
        flow.setStatus(StatusEnum.VALID.getCode());
        flow.setRemark(type.getDescription());
        flow.setCreator(operator);
        flow.setCreateTime(new Date());
        flow.setOperator(operator);
        flow.setOperateTime(new Date());
        return flow;
    }


    public void changeTeacher(long courseId, long newTeacherId) {
        CoursePo coursePo = courseService.selectByPrimaryKey(courseId);
        Assert.notNull(coursePo, "课程不存在，id：" + courseId);
        this.courseSchedulePoMapper.changeTeacher(courseId, newTeacherId);
    }

    /**
     * 根据ids 查询课程时间
     *
     * @param ids
     * @return java.util.List<com.nmt.education.pojo.po.CourseSchedulePo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/11 22:54
     */
    public List<CourseSchedulePo> queryByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return this.courseSchedulePoMapper.queryByIds(ids);
    }

    /**
     * 课程签到页面 select组件
     *
     * @param id        课程id
     * @param logInUser 登录人
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/28 22:18
     */
    public List<CourseSchedulePo> signInSelect(Long id, Integer logInUser) {
        CourseSchedulePo po = selectByPrimaryKey(id);
        Assert.notNull(po, "课表信息为空，id：" + id);
        return this.courseSchedulePoMapper.queryByCourseId(id);
    }

    /**
     * 课程签到页面数据
     *
     * @param courseScheduleId 课程日程id
     * @param logInUser        登录人
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/28 22:18
     */
    public List<CourseSignInItem> signInList(Long courseScheduleId, Integer logInUser) {
        return registerationSummaryService.queryByCourseScheduleId(courseScheduleId);
    }

    /**
     * 课程签到页面 --默认页面
     *
     * @param courseId  课程日程id
     * @param logInUser 登录人
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/28 22:18
     */
    @Deprecated
    public CourseSignInVo signInDefault(Long courseId, Integer logInUser) {
        CourseSignInVo vo = new CourseSignInVo();
        vo.setCourseId(courseId);
        CoursePo coursePo = courseService.selectByPrimaryKey(courseId);
        Assert.notNull(coursePo, "课程不存在，id：" + courseId);
        vo.setCourseName(coursePo.getName());
        List<CourseSchedulePo> courseSchedulePoList = courseSchedulePoMapper.queryByCourseId(courseId);
        vo.setCourseSchedule(courseSchedulePoList.stream().
                filter(e -> Enums.SignInType.已签到.getCode().equals(e.getSignIn())).sorted(Comparator.comparing(CourseSchedulePo::getId).reversed())
                .findFirst().orElse(null));
        vo.setSignInVos(Objects.nonNull(vo.getCourseSchedule()) ? signInList(vo.getCourseSchedule().getId(), Consts.SYSTEM_USER) : Collections.EMPTY_LIST);
        return vo;
    }
}
