-- 新增年级
INSERT INTO `sys_config`( `type`, `type_code`, `type_desc`, `value`, `description`, `status`, `remark`, `creator`, `create_time`, `operator`, `operate_time`) VALUES ( 6, 'grade', '年级', 21, '高一(新)', 1, '', 0, now(), 0, now());
INSERT INTO `sys_config`( `type`, `type_code`, `type_desc`, `value`, `description`, `status`, `remark`, `creator`, `create_time`, `operator`, `operate_time`) VALUES ( 6, 'grade', '年级', 22, '高二(新)', 1, '', 0, now(), 0, now());
INSERT INTO `sys_config`( `type`, `type_code`, `type_desc`, `value`, `description`, `status`, `remark`, `creator`, `create_time`, `operator`, `operate_time`) VALUES ( 6, 'grade', '年级', 23, '高三(新)', 1, '', 0, now(), 0, now());

-- 年级配置
INSERT INTO `grade_authorization`( `name`, `code`, `user_id`, `remark`, `status`, `creator`, `create_time`, `operator`, `operate_time`)
select description,value ,11000001,description , 1, 0, now(), 0, now()
from sys_config where type_code ='grade' and status = 1
