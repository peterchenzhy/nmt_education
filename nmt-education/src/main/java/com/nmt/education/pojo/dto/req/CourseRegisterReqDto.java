package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class CourseRegisterReqDto {

    @ApiModelProperty(value = "数据id,仅编辑使用")
    private Long id;
    @ApiModelProperty(value = "编辑标志；0：无变化，1：新增，2：编辑，3：需要删除")
    private Integer editFlag = 0;

    @ApiModelProperty(value = "学生id")
    @NotNull(message = "学生不能为空")
    private Long studentId;

    @ApiModelProperty(value = "课程id")
    @NotNull(message = "课程能为空")
    private Long courseId;

    @ApiModelProperty(value="报名类型")
    private Integer registrationType;

    @ApiModelProperty(value="订单状态")
    private Integer registrationStatus;

    @ApiModelProperty(value="费用状态")
    private Integer feeStatus;

    @NotNull(message = "报名次数不能为空")
    @Range(min = 1, message = "报名次数必须大于0")
    @ApiModelProperty(value = "报名次数")
    private Integer times;
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "课程时间ids")
    private List<Long> courseScheduleIds;

    @ApiModelProperty(value = "支付信息")
    private List<RegisterExpenseDetailReqDto> registerExpenseDetail;


}
