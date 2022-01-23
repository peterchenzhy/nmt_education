package com.nmt.education.service.student;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.*;
import com.nmt.education.pojo.dto.req.AccountEditReqDto;
import com.nmt.education.pojo.dto.req.StudentReqDto;
import com.nmt.education.pojo.dto.req.StudentSearchReqDto;
import com.nmt.education.pojo.po.StudentAccountFlowPo;
import com.nmt.education.pojo.po.StudentAccountPo;
import com.nmt.education.pojo.po.StudentPo;
import com.nmt.education.pojo.vo.StudentAccountDetailVo;
import com.nmt.education.pojo.vo.StudentAccountVo;
import com.nmt.education.pojo.vo.StudentVo;
import com.nmt.education.service.CodeService;
import com.nmt.education.service.authorization.campus.CampusAuthorizationService;
import com.nmt.education.service.student.account.StudentAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Service
@Slf4j
public class StudentService {

    @Resource
    private StudentPoMapper studentPoMapper;

    @Autowired
    private CodeService codeService;

    @Autowired
    @Lazy
    private StudentService self;
    @Autowired
    private CampusAuthorizationService campusAuthorizationService;
    @Autowired
    private StudentAccountService studentAccountService;

    public Boolean studentManager(Integer operator, StudentReqDto dto) {
        campusAuthorizationService.getCampusAuthorization(operator, dto.getCampus());
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        switch (editFlag) {
            case 新增:
                self.newStudent(operator, dto);
                break;
            case 修改:
                self.editStudent(operator, dto);
                break;
            case 需要删除:
                self.delStudent(operator, dto);
                break;
            case 无变化:
                break;
            default:
                log.error("studentService请求数据不合规，无法辨认editFlag！" + dto);
                break;
        }

        return true;
    }

    private void delStudent(Integer operator, StudentReqDto dto) {
        invalidByPrimaryKey(operator, dto.getId());
    }

    private void invalidByPrimaryKey(Integer operator, Long id) {
        if (Objects.isNull(id)) {
            log.error("invalidByPrimaryKey id is null");
            return;
        }
        this.studentPoMapper.invalidByPrimaryKey(operator, id);

    }


    /**
     * 新增学生
     *
     * @param studentReqDto
     * @return java.lang.Boolean
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @summary 新增学生
     * @since 2020/3/31 22:18
     */
    public Boolean newStudent(Integer operator, StudentReqDto studentReqDto) {
        StudentPo studentPo = new StudentPo();
        studentPo.setStudentCode(codeService.generateNewStudentCode(studentReqDto.getCampus()));
        studentPo.setName(studentReqDto.getName());
        studentPo.setBirthday(studentReqDto.getBirthday());
        studentPo.setSchool(studentReqDto.getSchool());
        studentPo.setGrade(studentReqDto.getGrade());
        studentPo.setPhone(studentReqDto.getPhone());
        studentPo.setSex(studentReqDto.getSex());
        studentPo.setCampus(studentReqDto.getCampus());
        studentPo.setRemark(studentReqDto.getRemark());
        studentPo.setStatus(StatusEnum.VALID.getCode());
        studentPo.setCreator(operator);
        studentPo.setCreateTime(new Date());
        studentPo.setOperator(operator);
        studentPo.setOperateTime(new Date());
        return this.insertSelective(studentPo) > 0;
    }


    /**
     * 编辑学生
     *
     * @param operator
     * @param dto
     * @return java.lang.Boolean
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/5 19:59
     */
    public Boolean editStudent(Integer operator, StudentReqDto dto) {
        Assert.notNull(dto.getId(), "编辑学生缺少id");
        StudentPo studentPo = selectByPrimaryKey(dto.getId());
        Assert.notNull(studentPo, "学生信息不存在" + dto.getId());
        studentPo.setName(dto.getName());
        studentPo.setBirthday(dto.getBirthday());
        studentPo.setSchool(dto.getSchool());
        studentPo.setGrade(dto.getGrade());
        studentPo.setPhone(dto.getPhone());
        studentPo.setSex(dto.getSex());
        studentPo.setCampus(dto.getCampus());
        studentPo.setRemark(dto.getRemark());
        studentPo.setOperator(operator);
        studentPo.setOperateTime(new Date());
        return this.updateByPrimaryKeySelective(studentPo) > 0;
    }

    /**
     * 搜索 学生
     *
     * @param operator
     * @param dto
     * @return com.nmt.education.pojo.vo.StudentVo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/4 9:05
     */
    public PageInfo<StudentVo> search(Integer operator, StudentSearchReqDto dto) {
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(operator);
        PageInfo<StudentPo> pageInfo;
        if (StringUtils.hasLength(dto.getPhone())) {
            pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.queryByPhone(dto.getPhone(), campusList));
        } else {
            if (StringUtils.hasLength(dto.getName())) {
                pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.searchFuzzy(dto.getName(), campusList));
            } else {
                pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.queryByPhone(null, campusList));
            }
        }
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return new PageInfo<>(Collections.emptyList());
        }
        List<StudentVo> voList = new ArrayList<>(pageInfo.getList().size());
        pageInfo.getList().stream().forEach(e -> voList.add(po2vo(e)));
        PageInfo voPage = new PageInfo();
        BeanUtils.copyProperties(pageInfo, voPage);
        voPage.setList(voList);
        PageHelper.clearPage();
        return voPage;
    }

    private List<StudentPo> queryByPhone(String phone, List<Integer> campusList) {

        return this.studentPoMapper.query(phone, campusList);
    }


    public int insertSelective(StudentPo record) {
        return studentPoMapper.insertSelective(record);
    }


    public StudentPo selectByPrimaryKey(Long id) {
        return studentPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(StudentPo record) {
        return studentPoMapper.updateByPrimaryKeySelective(record);
    }


    /**
     * 学生模糊搜索 ，左匹配 不含联系方式
     *
     * @param name
     * @param campusList
     * @return
     */
    public List<StudentVo> searchFuzzy(String name, List<Integer> campusList) {
        if (StringUtils.hasLength(name)) {
            List<StudentPo> list = this.studentPoMapper.queryFuzzy(name,campusList);
            List<StudentVo> result = new ArrayList<>(list.size());
            list.stream().filter(e -> campusList.contains(e.getCampus())).forEach(e -> {
                StudentVo vo = po2vo(e);
                result.add(vo);
            });
            return result;
        }
        return Collections.emptyList();
    }

    public List<StudentVo> searchFuzzy(Integer logInUser, String name) {
        return this.searchFuzzy(name, campusAuthorizationService.getCampusAuthorization(logInUser));
    }


    public StudentVo detail(Long id) {
        Assert.notNull(id, "学生明细缺少学生,id:" + id);
        StudentPo po = selectByPrimaryKey(id);
        Assert.notNull(po, "学生明细不存在，id：" + id);
        return po2vo(po);
    }

    public StudentVo po2vo(StudentPo po) {
        StudentVo vo = new StudentVo();
        BeanUtils.copyProperties(po, vo);
        return vo;
    }


    public List<StudentPo> queryByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return this.studentPoMapper.queryByIds(ids);
    }


    public PageInfo<StudentAccountVo> accountPage(Long studentId, Integer pageNo, Integer pageSize) {
        return PageHelper.startPage(pageNo, pageSize).doSelectPageInfo(() -> this.studentAccountService.queryAccount(studentId));
    }

    /**
     * 学生账户
     *
     * @param studentId
     * @return
     */
    public StudentAccountVo account(Long studentId) {
        if (Objects.isNull(studentId)) {
            return null;
        }
        final List<StudentAccountVo> studentAccountVos = studentAccountService.queryAccount(studentId);
        if (CollectionUtils.isEmpty(studentAccountVos)) {
            return null;
        } else {
            return studentAccountVos.get(0);
        }
    }

    /**
     * 编辑学生账户
     *
     * @param accountEditReqDto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/10/3 22:21
     */
    @Transactional(rollbackFor = Exception.class)
    public void accountEdit(AccountEditReqDto accountEditReqDto, int logInUser) {
        StudentAccountPo accountPo = studentAccountService.querybyUserId(accountEditReqDto.getStudentId());
        Assert.notNull(accountPo, "学生账户不存在！");
        String prevAmount = accountPo.getAmount();
        accountPo.setAmount(accountEditReqDto.getAmount());
        accountPo.setRemark(accountEditReqDto.getRemark());
        studentAccountService.updateByVersion(accountPo);
        //插入流水
        StudentAccountFlowPo flowPo = studentAccountService.generateFlow(logInUser, accountPo.getId(), accountEditReqDto.getAmount(),
                ExpenseDetailFlowTypeEnum.编辑, -1L,prevAmount, Consts.账户金额更新模板 + accountEditReqDto.getRemark());
        flowPo.setSource(AccountFlowSourceEnum.后台编辑.getCode());
        studentAccountService.insertFlow(flowPo);
    }


    public PageInfo<StudentAccountDetailVo> accountDetail(Long studentId, Integer pageNo, Integer pageSize) {
        Assert.notNull(studentId, "学生id不能为空");
        StudentPo studentPo = this.selectByPrimaryKey(studentId);
        Assert.notNull(studentPo, "学生信息不存在");
        PageInfo<StudentAccountDetailVo> pageInfo = new PageInfo<>();
        StudentAccountPo accountPo = this.studentAccountService.querybyUserId(studentId);
        if (accountPo == null) {
            return new PageInfo<>();
        }
        PageInfo<StudentAccountFlowPo> flowPageInfo =
                PageHelper.startPage(pageNo, pageSize).doSelectPageInfo(() -> studentAccountService.queryAccountFlowByAccountId(accountPo.getId()));
        if (!CollectionUtils.isEmpty(flowPageInfo.getList())) {
            BeanUtils.copyProperties(flowPageInfo, pageInfo);
            pageInfo.setList(flowPageInfo.getList().stream().map(po -> {
                StudentAccountDetailVo vo = new StudentAccountDetailVo();
                vo.setStudentId(studentPo.getId());
                vo.setStudentName(studentPo.getName());
                vo.setId(po.getId());
                vo.setStudentAccountId(po.getStudentAccountId());
                vo.setType(ExpenseDetailFlowTypeEnum.code2Desc(po.getType()));
                vo.setRefId(po.getRefId());
                vo.setAmount(po.getAmount());
                vo.setBeforeAmount(po.getBeforeAmount());
                vo.setRemark(po.getRemark());
                vo.setCreateTime(po.getCreateTime());
                return vo;
            }).collect(Collectors.toList()));
        }

        return pageInfo;
    }
}
