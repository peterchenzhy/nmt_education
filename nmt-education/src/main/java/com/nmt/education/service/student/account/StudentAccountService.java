package com.nmt.education.service.student.account;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.nmt.education.commmons.*;
import com.nmt.education.pojo.po.*;
import com.nmt.education.pojo.vo.StudentAccountVo;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailService;
import com.nmt.education.service.course.registeration.summary.RegisterationSummaryService;
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
     * @param course
     */
    public void addByCourseFinish(Integer logInUser, CoursePo course) {
        long courseId = course.getId();
        Map<Long, List<RegisterationSummaryPo>> noConsumptionMap = this.registerationSummaryService.queryByCourseId(courseId, Enums.SignInType.CAN_REFUND)
                .stream().collect(Collectors.groupingBy(RegisterationSummaryPo::getStudentId));
        if (CollectionUtils.isEmpty(noConsumptionMap)) {
            return;
        }
        noConsumptionMap.forEach((k, v) -> {
            final RegistrationExpenseDetailPo expenseDetailPo = registrationExpenseDetailService.queryRegisterId(v.get(0).getCourseRegistrationId())
                    .stream().filter(e -> Consts.FEE_TYPE_普通单节费用.equals(e.getFeeType())).findAny().orElse(null);
            if (Objects.nonNull(expenseDetailPo)) {
                BigDecimal amount = NumberUtil.String2Dec(expenseDetailPo.getDiscount())
                        .multiply(NumberUtil.String2Dec(expenseDetailPo.getPerAmount())).multiply(new BigDecimal(v.size()));
                self.addAmount(logInUser, k, amount, expenseDetailPo.getRegistrationId(),String.format(Consts.结转进学生账户REMARK,course.getName(),
                        amount.toPlainString()),AccountFlowSourceEnum.结转);
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void addAmount(Integer logInUser, Long userId, BigDecimal amount, Long registerId, String remark,AccountFlowSourceEnum sourceEnum) {
        StudentAccountPo accountPo = querybyUserId(userId);
        if (Objects.isNull(accountPo)) {
            //新建账户
            newStudentAccountPo(logInUser, userId, amount, registerId,String.format(Consts.新增结余账户模板,remark),sourceEnum);
        } else {
            String lastAmount = accountPo.getAmount();
            accountPo.setAmount(amount.add(NumberUtil.String2Dec(accountPo.getAmount())).toPlainString());
            accountPo.setOperator(logInUser);
            accountPo.setOperateTime(new Date());
            this.updateByVersion(accountPo);
            StudentAccountFlowPo flowPo = generateFlow(logInUser, accountPo.getId(), accountPo.getAmount(), ExpenseDetailFlowTypeEnum.编辑, registerId,
                    lastAmount, remark);
            flowPo.setSource(sourceEnum.getCode());
            insertFlow(flowPo);

        }
    }

    public void insertFlow(StudentAccountFlowPo flowPo) {
        this.studentAccountFlowPoMapper.insertSelective(flowPo);
    }

    public int updateByVersion(StudentAccountPo accountPo) {
        Assert.notNull(accountPo, "账号不存在！");
        return this.studentAccountPoMapper.updateByVersion(accountPo);
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
    private StudentAccountPo newStudentAccountPo(Integer logInUser, Long userId, BigDecimal amount, Long registerId,String remark
    ,AccountFlowSourceEnum sourceEnum) {
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
        StudentAccountFlowPo flowPo = generateFlow(logInUser, po.getId(), po.getAmount(), ExpenseDetailFlowTypeEnum.新增记录, registerId,
                BigDecimal.ZERO.toPlainString(), remark);
        flowPo.setSource(sourceEnum.getCode());
        insertFlow(flowPo);

        return po;
    }

    /**
     * 生成账户流水
     *
     * @param logInUser        登录人
     * @param studentAccountId 学生id
     * @param amount           账户金额
     * @param type             类型
     * @param registerId       报名id
     * @param lastAmount       账户修改前金额
     * @param remark           备注
     * @return
     */
    public StudentAccountFlowPo generateFlow(Integer logInUser, long studentAccountId, String amount, ExpenseDetailFlowTypeEnum type, Long registerId,
                                             String lastAmount, String remark) {
        StudentAccountFlowPo flowPo = new StudentAccountFlowPo();
        flowPo.setStudentAccountId(studentAccountId);
        flowPo.setType(type.getCode());
        flowPo.setRefId(registerId);
        flowPo.setAmount(amount);
        flowPo.setBeforeAmount(lastAmount);
        flowPo.setStatus(StatusEnum.VALID.getCode());
        flowPo.setCreator(logInUser);
        flowPo.setCreateTime(new Date());
        flowPo.setOperator(logInUser);
        flowPo.setOperateTime(new Date());
        flowPo.setRemark(Strings.nullToEmpty(remark));
        //消耗的source为-1 后面根据业务可能再次赋值
        flowPo.setSource(-1);

        return flowPo;
    }

    public StudentAccountPo querybyUserId(Long userId) {
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

    public List<StudentAccountVo> queryAccount(Long studentId) {
        return this.studentAccountPoMapper.queryAccount(studentId);
    }


    public void addFlow(List<StudentAccountFlowPo> accountFlowList) {
        if (CollectionUtils.isEmpty(accountFlowList)) {
            return;
        }
        this.studentAccountFlowPoMapper.batchInsert(accountFlowList);
    }

    /**
     * 根据报名记录查询 结余消耗流水
     *
     * @param registerId
     * @return
     */
    public List<StudentAccountFlowPo> queryFlowByRegisterId(Long registerId) {
        if (Objects.isNull(registerId)) {
            return Collections.emptyList();
        }
        return this.studentAccountFlowPoMapper.queryByRegisterIds(Lists.newArrayList(registerId));
    }
    public List<StudentAccountFlowPo> queryFlowByRegisterIds(List<Long> registerIds) {
        if (CollectionUtils.isEmpty(registerIds)) {
            return Collections.emptyList();
        }
        return this.studentAccountFlowPoMapper.queryByRegisterIds(registerIds);
    }

    public List<StudentAccountFlowPo> queryAccountFlowByAccountId(Long accountId) {

        List<StudentAccountFlowPo> list = this.studentAccountFlowPoMapper.queryByAccountId(accountId);
        return list;
    }
}
