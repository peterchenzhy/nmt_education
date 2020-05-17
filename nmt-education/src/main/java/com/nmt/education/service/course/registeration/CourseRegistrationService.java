package com.nmt.education.service.course.registeration;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.NumberUtil;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.dto.req.CourseRegisterReqDto;
import com.nmt.education.pojo.dto.req.RegisterExpenseDetailReqDto;
import com.nmt.education.pojo.dto.req.RegisterSearchReqDto;
import com.nmt.education.pojo.dto.req.RegisterSummarySearchDto;
import com.nmt.education.pojo.po.*;
import com.nmt.education.pojo.vo.CourseRegistrationListVo;
import com.nmt.education.pojo.vo.CourseRegistrationVo;
import com.nmt.education.pojo.vo.RegisterSummaryVo;
import com.nmt.education.pojo.vo.StudentVo;
import com.nmt.education.service.CodeService;
import com.nmt.education.service.course.CourseService;
import com.nmt.education.service.course.registeration.summary.RegisterationSummaryService;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import com.nmt.education.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseRegistrationService {

    @Resource
    private CourseRegistrationPoMapper courseRegistrationPoMapper;
    @Autowired
    @Lazy
    private CourseRegistrationService self;

    @Autowired
    private RegistrationExpenseDetailService registrationExpenseDetailService;

    @Autowired
    private RegisterationSummaryService registerationSummaryService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CodeService codeService;
    @Autowired
    private CourseScheduleService courseScheduleService;


    /**
     * 课程报名
     *
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 10:53
     */
    public void register(CourseRegisterReqDto dto, int updator) {
        registerCheck(dto);
        self.startRegisterTransaction(dto, updator);

    }

    @Transactional(rollbackFor = Exception.class)
    public void startRegisterTransaction(CourseRegisterReqDto dto, int updator) {
        //生成报名记录
        CourseRegistrationPo courseRegistrationPo = generateCourseRegistrationPo(dto, updator);
        this.insertSelective(courseRegistrationPo);

        //缴费记录明细
        List<RegistrationExpenseDetailPo> expenseDetailPoList = generateRegisterExpenseDetail(dto.getRegisterExpenseDetail(), updator,
                courseRegistrationPo);
        registrationExpenseDetailService.batchInsert(expenseDetailPoList);

        //汇总课表
        registerationSummaryService.batchInsert(generateRegisterationSummary(dto, updator, courseRegistrationPo));
    }

    /**
     * 生成 报名信息汇总表
     * 用于统计课程消耗的
     *
     * @param dto
     * @param updator
     * @param courseRegistrationPo
     * @return java.util.List<com.nmt.education.pojo.po.RegisterationSummaryPo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 21:30
     */
    private List<RegisterationSummaryPo> generateRegisterationSummary(CourseRegisterReqDto dto, int updator, CourseRegistrationPo courseRegistrationPo) {
        List<Long> courseScheduleIds = dto.getCourseScheduleIds();
        Assert.isTrue(!CollectionUtils.isEmpty(courseScheduleIds), "报名时不存在上课信息");
        List<RegisterationSummaryPo> list = new ArrayList<>(courseScheduleIds.size());
        courseScheduleIds.stream().forEach(e -> {
            RegisterationSummaryPo registerationSummaryPo = registerationSummaryService.dto2po(dto, updator, courseRegistrationPo, e);
            list.add(registerationSummaryPo);
        });
        return list;
    }


    private void registerCheck(CourseRegisterReqDto dto) {
        Assert.notNull(studentService.selectByPrimaryKey(dto.getStudentId()), "学生信息不存在！id:" + dto.getStudentId());
        Assert.notNull(courseService.selectByPrimaryKey(dto.getCourseId()), "学生信息不存在！id:" + dto.getCourseId());
        Assert.notEmpty(dto.getCourseScheduleIds(), "报名时间必填！id:" + dto.getCourseId());
        Assert.isNull(queryByCourseStudent(dto.getCourseId(), dto.getStudentId()), "报名记录已经存在！id:" + dto.getCourseId());
    }

    /**
     * 根据课程和学生查找报名记录
     *
     * @param courseId
     * @param studentId
     * @return com.nmt.education.pojo.po.CourseRegistrationPo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/8 22:38
     */
    public CourseRegistrationListVo queryByCourseStudent(Long courseId, Long studentId) {
        return this.courseRegistrationPoMapper.queryByCourseStudent(courseId, studentId);
    }

    private List<RegistrationExpenseDetailPo> generateRegisterExpenseDetail(List<RegisterExpenseDetailReqDto> expenseDetailList, int updator,
                                                                            CourseRegistrationPo courseRegistrationPo) {
        Assert.isTrue(!CollectionUtils.isEmpty(expenseDetailList), "报名时不存在费用信息");
        List<RegistrationExpenseDetailPo> resultList = new ArrayList<>(expenseDetailList.size());
        expenseDetailList.stream().forEach(e -> {
            resultList.add(registrationExpenseDetailService.dto2po(updator, courseRegistrationPo, e));
        });
        return resultList;
    }


    /**
     * 报名记录搜索
     *
     * @param dto
     * @param logInUser
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/5 15:45
     */
    public PageInfo<CourseRegistrationListVo> registerSearch(RegisterSearchReqDto dto, Integer logInUser) {
        return PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.courseRegistrationPoMapper.queryByDto(dto));
    }

    /**
     * 生成 报名记录po
     *
     * @param dto
     * @param updator
     * @return com.nmt.education.pojo.po.CourseRegistrationPo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 14:35
     */
    private CourseRegistrationPo generateCourseRegistrationPo(CourseRegisterReqDto dto, int updator) {

        CourseRegistrationPo courseRegistrationPo = new CourseRegistrationPo();
        courseRegistrationPo.setRegistrationNumber(codeService.generateNewRegistrationNumber(dto.getCampus()));
        courseRegistrationPo.setCourseId(dto.getCourseId());
        courseRegistrationPo.setStudentId(dto.getStudentId());
        courseRegistrationPo.setTimes(dto.getCourseScheduleIds().size());
        courseRegistrationPo.setRegistrationType(dto.getRegistrationType());
        courseRegistrationPo.setRegistrationStatus(dto.getRegistrationStatus());
        courseRegistrationPo.setFeeStatus(dto.getFeeStatus());
        courseRegistrationPo.setStatus(StatusEnum.VALID.getCode());
        courseRegistrationPo.setRemark(dto.getRemark());
        courseRegistrationPo.setCreator(updator);
        courseRegistrationPo.setCreateTime(new Date());
        courseRegistrationPo.setOperator(updator);
        courseRegistrationPo.setOperateTime(new Date());
        courseRegistrationPo.setTotalAmount(calculateTotalAmount(dto.getRegisterExpenseDetail()));
        courseRegistrationPo.setBalanceAmount(courseRegistrationPo.getTotalAmount());
        return courseRegistrationPo;
    }

    /**
     * 计算订单总额
     *
     * @param registerExpenseDetail
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 21:54
     */
    private String calculateTotalAmount(List<RegisterExpenseDetailReqDto> registerExpenseDetail) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal checkResult = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(registerExpenseDetail)) {
            for (RegisterExpenseDetailReqDto e : registerExpenseDetail) {
                result = result.add(NumberUtil.String2Dec(e.getAmount()));
                checkResult = checkResult.add(NumberUtil.String2Dec(e.getDiscount())
                        .multiply(NumberUtil.String2Dec(e.getPerAmount()))
                        .multiply(new BigDecimal(e.getCount())));
            }
        }
        Assert.isTrue(result.compareTo(checkResult) == 0, "费用金额，与计算金额不符");
        return result.toPlainString();
    }

    /**
     * 退费
     *
     * @param id
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 10:53
     */
    public void registerDel(Long id, int updator) {

        // TODO: 2020/5/2 逻辑有问题  待定

        CourseRegistrationPo courseRegistrationPo = selectByPrimaryKey(id);
        Assert.notNull(courseRegistrationPo, "报名信息不存在，id：" + id);
        CoursePo coursePo = courseService.selectByPrimaryKey(courseRegistrationPo.getCourseId());
        Assert.notNull(coursePo, "课程信息不存在，id：" + courseRegistrationPo.getCourseId());
        //无效报名记录

        //无效课程消耗

        //无效费用信息
    }


    public PageInfo<RegisterSummaryVo> registerSummary(RegisterSummarySearchDto dto, Integer logInUser) {
        if (Objects.nonNull(dto.getEndDate())) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        if (Objects.nonNull(dto.getRegisterEndDate())) {
            dto.setRegisterEndDate(DateUtil.parseCloseDate(dto.getRegisterEndDate()));
        }
        PageInfo<RegisterSummaryVo> pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> queryBySearchDto(dto));
        return pageInfo;
    }

    /**
     * 报名记录查询 ，课时消耗查询
     *
     * @param dto
     * @return java.util.List<com.nmt.education.pojo.vo.RegisterSummaryVo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/11 22:02
     */
    private List<RegisterSummaryVo> queryBySearchDto(RegisterSummarySearchDto dto) {

        return this.registerationSummaryService.queryBySearchDto(dto);
    }


    public int insertSelective(CourseRegistrationPo record) {
        return courseRegistrationPoMapper.insertSelective(record);
    }


    public CourseRegistrationPo selectByPrimaryKey(Long id) {
        return courseRegistrationPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(CourseRegistrationPo record) {
        return courseRegistrationPoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<CourseRegistrationPo> list) {
        return courseRegistrationPoMapper.updateBatch(list);
    }


    public int updateBatchSelective(List<CourseRegistrationPo> list) {
        return courseRegistrationPoMapper.updateBatchSelective(list);
    }


    public int batchInsert(List<CourseRegistrationPo> list) {
        return courseRegistrationPoMapper.batchInsert(list);
    }


    public int insertOrUpdate(CourseRegistrationPo record) {
        return courseRegistrationPoMapper.insertOrUpdate(record);
    }


    public int insertOrUpdateSelective(CourseRegistrationPo record) {
        return courseRegistrationPoMapper.insertOrUpdateSelective(record);
    }


    public CourseRegistrationVo registerDetail(Long id, Integer logInUser) {
        if (Objects.isNull(id)) {
            return null;
        }
        CourseRegistrationVo vo = this.courseRegistrationPoMapper.queryVoById(id);
        Assert.notNull(vo, "无法查询到报名记录，id:" + id);
        vo.setCourse(courseService.selectByPrimaryKey(vo.getCourseId()));
        vo.setStudent(studentService.detail(vo.getStudentId()));
        List<RegisterationSummaryPo> registerationSummaryPoList = registerationSummaryService.queryByRegisterId(id);
        vo.setCourseScheduleList(courseScheduleService.queryByIds(registerationSummaryPoList.stream().map(e -> e.getCourseScheduleId()).collect(Collectors.toList())));
        vo.setRegisterExpenseDetail(registrationExpenseDetailService.queryRegisterId(id));
        return vo;
    }

    /**
     * 根据课程id 查询学生报名情况
     *
     * @param courseId 课程id
     * @return java.util.List<com.nmt.education.pojo.vo.StudentVo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/14 23:36
     */
    public List<StudentVo> registerStudent(Long courseId) {
        List<CourseRegistrationPo> registrationList = this.courseRegistrationPoMapper.queryByCourseId(courseId);
        if (CollectionUtils.isEmpty(registrationList)) {
            return Collections.emptyList();
        }
        List<StudentPo> studentPoList = studentService.queryByIds(registrationList.stream().map(e -> e.getStudentId()).collect(Collectors.toList()));
        List<StudentVo> voList = new ArrayList<>(studentPoList.size());
        studentPoList.stream().forEach(e -> voList.add(studentService.po2vo(e)));
        return voList;
    }
}
