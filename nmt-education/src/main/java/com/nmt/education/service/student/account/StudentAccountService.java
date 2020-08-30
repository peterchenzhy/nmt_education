package com.nmt.education.service.student.account;

import com.google.common.collect.Lists;
import com.nmt.education.commmons.*;
import com.nmt.education.pojo.dto.req.StudentSearchReqDto;
import com.nmt.education.pojo.po.RegisterationSummaryPo;
import com.nmt.education.pojo.po.RegistrationExpenseDetailPo;
import com.nmt.education.pojo.po.StudentAccountFlowPo;
import com.nmt.education.pojo.po.StudentAccountPo;
import com.nmt.education.pojo.vo.StudentAccountVo;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailService;
import com.nmt.education.service.course.registeration.summary.RegisterationSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 学生账户
 */
@Service
public class StudentAccountService {

    @Resource
    private StudentAccountPoMapper studentAccountPoMapper;
    @Resource
    private StudentAccountFlowPoMapper studentAccountFlowPoMapper;

    @Autowired
    @Lazy
    private StudentAccountService self;

    @Autowired
    @Lazy
    private RegisterationSummaryService registerationSummaryService;
    @Autowired
    @Lazy
    private RegistrationExpenseDetailService registrationExpenseDetailService;


    /**
     * 根据课程结转增加结余数据
     *
     * @param logInUser
     * @param courseId
     */
    public void addByCourseFinish(Integer logInUser, Long courseId) {
        Map<Long, List<RegisterationSummaryPo>> noConsumptionMap = this.registerationSummaryService.queryByCourseId(courseId,
                Lists.newArrayList(Enums.SignInType.未签到.getCode(), Enums.SignInType.请假.getCode()))
                .stream().collect(Collectors.groupingBy(RegisterationSummaryPo::getStudentId));
        if (CollectionUtils.isEmpty(noConsumptionMap)) {
            return;
        }
        noConsumptionMap.forEach((k, v) -> {
            final RegistrationExpenseDetailPo expenseDetailPo = registrationExpenseDetailService.queryRegisterId(v.get(0).getCourseRegistrationId())
                    .stream().filter(e -> Consts.FEE_TYPE_普通单节费用.equals(e.getFeeType())).findAny().orElseGet(null);
            if (Objects.nonNull(expenseDetailPo)) {
                BigDecimal amount = NumberUtil.String2Dec(expenseDetailPo.getDiscount())
                        .multiply(NumberUtil.String2Dec(expenseDetailPo.getPerAmount())).multiply(new BigDecimal(v.size()));
                self.addAmount(logInUser, k, amount, expenseDetailPo.getRegistrationId());
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Async
    public void addAmount(Integer logInUser, Long userId, BigDecimal amount, Long registerId) {
        StudentAccountPo accountPo = querybyUserId(userId);
        if (Objects.isNull(accountPo)) {
            accountPo = newStudentAccountPo(logInUser, userId, amount, registerId);
        }else{
            String lastAmount = accountPo.getAmount();
            accountPo.setAmount(amount.add(NumberUtil.String2Dec(accountPo.getAmount())).toPlainString());
            accountPo.setOperator(logInUser);
            accountPo.setOperateTime(new Date());
            this.updateByVersion(accountPo);
            Account2Flow(logInUser,accountPo,ExpenseDetailFlowTypeEnum.编辑,registerId,lastAmount);

        }
    }

    private void updateByVersion(StudentAccountPo accountPo) {
        this.studentAccountPoMapper.updateByVersion(accountPo);
    }

    /**
     * 新增 账户
     *
     * @param logInUser
     * @param userId
     * @param amount
     * @param registerId
     * @return com.nmt.education.pojo.po.StudentAccountPo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/8/30 21:57
     */
    private StudentAccountPo newStudentAccountPo(Integer logInUser, Long userId, BigDecimal amount, Long registerId) {
        StudentAccountPo po = new StudentAccountPo();
        po.setUserId(userId);
        po.setAmount(amount.toPlainString());
        po.setStatus(StatusEnum.VALID.getCode());
        po.setCreator(logInUser);
        po.setCreateTime(new Date());
        po.setOperator(logInUser);
        po.setOperateTime(new Date());
        po.setVersion(1);
        this.insertSelective(po);
        Account2Flow(logInUser, po, ExpenseDetailFlowTypeEnum.新增记录 ,registerId, BigDecimal.ZERO.toPlainString());

        return po;
    }

    private StudentAccountFlowPo Account2Flow(Integer logInUser, StudentAccountPo po, ExpenseDetailFlowTypeEnum type, Long registerId,
                                              String lastAmount) {
        StudentAccountFlowPo flowPo = new StudentAccountFlowPo();
        flowPo.setStudentAccountId(po.getId());
        flowPo.setType(type.getCode());
        flowPo.setRefId(registerId);
        flowPo.setAmount(po.getAmount());
        flowPo.setBeforeAmount(lastAmount);
        flowPo.setStatus(StatusEnum.VALID.getCode());
        flowPo.setCreator(logInUser);
        flowPo.setCreateTime(new Date());
        flowPo.setOperator(logInUser);
        flowPo.setOperateTime(new Date());
        this.studentAccountFlowPoMapper.insertSelective(flowPo);
        return flowPo;
    }

    private StudentAccountPo querybyUserId(Long userId) {
        return this.studentAccountPoMapper.querybyUserId(userId);
    }


    //-----------------------------------------------------------------//

    public int insertSelective(StudentAccountPo record) {
        return studentAccountPoMapper.insertSelective(record);
    }


    public StudentAccountPo selectByPrimaryKey(Long id) {
        return studentAccountPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(StudentAccountPo record) {
        return studentAccountPoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatchSelective(List<StudentAccountPo> list) {
        return studentAccountPoMapper.updateBatchSelective(list);
    }


    public int batchInsert(List<StudentAccountPo> list) {
        return studentAccountPoMapper.batchInsert(list);
    }

    public List<StudentAccountVo> queryAccount(Long  studentId) {
        return this.studentAccountPoMapper.queryAccount(studentId);
    }
}
