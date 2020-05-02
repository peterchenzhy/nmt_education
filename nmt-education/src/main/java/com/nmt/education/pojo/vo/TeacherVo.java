package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.TeacherPo;
import com.nmt.education.pojo.po.TeacherSalaryConfigPo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Setter
@Getter
@ToString
public class TeacherVo extends TeacherPo {

    private List<TeacherSalaryConfigPo> salaryConfigList = Collections.emptyList();
}
