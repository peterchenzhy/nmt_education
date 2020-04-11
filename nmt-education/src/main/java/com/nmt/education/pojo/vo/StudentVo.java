package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.StudentPo;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;


@Getter
@Setter
public class StudentVo extends StudentPo {

    List<StudentContactVo> contractList = Collections.EMPTY_LIST;
}
