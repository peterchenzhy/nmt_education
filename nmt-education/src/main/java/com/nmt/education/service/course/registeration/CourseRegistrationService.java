package com.nmt.education.service.course.registeration;

import com.nmt.education.commmons.NumberUtil;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.CourseRegisterReqDto;
import com.nmt.education.pojo.dto.req.RegisterExpenseDetailReqDto;
import com.nmt.education.pojo.po.CourseRegistrationPo;
import com.nmt.education.pojo.po.RegisterationSummaryPo;
import com.nmt.education.pojo.po.RegistrationExpenseDetailPo;
import com.nmt.education.service.CodeService;
import com.nmt.education.service.course.CourseService;
import com.nmt.education.service.course.registeration.summary.RegisterationSummaryService;
import com.nmt.education.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        courseRegistrationPo.setTimes(dto.getTimes());
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
     * 删除课程报名
     *
     * @param id
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/30 10:53
     */
    public void registerDel(Long id, int updator) {
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
}
