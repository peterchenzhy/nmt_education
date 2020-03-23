package com.nmt.education.service.sysconfig;

import com.alibaba.fastjson.JSON;
import com.nmt.education.BaseTest;
import com.nmt.education.pojo.po.SysConfigPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @Author: PeterChen
 * @Date: 2020/3/22 22:30
 * @Version 1.0
 */
@Slf4j
class SysConfigServiceTest extends BaseTest {
    @Autowired
    private SysConfigService sysConfigService;

    private SysConfigPo defaultPo = baseDBconfig();

    @Test
    public void initDBConfig() {
        List<SysConfigPo> list = new ArrayList<>();
        List<String[]> resource = Lists.newArrayList(
                new String[]{"7", "feeType", "费用类型","1", "普通单节费用"},
                new String[]{"7", "feeType", "费用类型","2", "试听费"},
                new String[]{"7", "feeType", "费用类型","3", "材料费"},
                new String[]{"7", "feeType", "费用类型","4", "试验费"}
        );

        resource.stream().forEach(e->{
            list.add(generateConfig(e));
        });
        System.out.println(JSON.toJSONString(list));
        sysConfigService.batchInsert(list);

    }

    private SysConfigPo generateConfig(String... str) {
        SysConfigPo po = SerializationUtils.clone(defaultPo);
        po.setType(Integer.valueOf( str[0]));
        po.setTypeCode(str[1]);
        po.setTypeDesc(str[2]);
        po.setValue(Integer.valueOf(str[3]));
        po.setDescription(str[4]);
        po.setRemark("");
        return po;
    }


    private SysConfigPo baseDBconfig() {
        SysConfigPo po = new SysConfigPo();
        po.setStatus(1);
        po.setCreator(0);
        po.setCreateTime(new Date());
        po.setOperator(0);
        po.setOperateTime(new Date());
        return po;
    }
}