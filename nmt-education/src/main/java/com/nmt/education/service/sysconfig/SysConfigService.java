package com.nmt.education.service.sysconfig;

import com.nmt.education.commmons.Consts;
import com.nmt.education.config.cache.CacheManagerConfig;
import com.nmt.education.pojo.po.SysConfigPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysConfigService {

    @Resource
    private SysConfigPoMapper sysConfigPoMapper;
    @Autowired
    @Lazy
    private SysConfigService self;

    @Cacheable(key = "'getAllConfigs'", value = CacheManagerConfig.EhCacheNames.CACHE_10MINS, cacheManager = CacheManagerConfig.CacheManagerNames.EHCACHE_CACHE_MANAGER)
    public List<SysConfigPo> getAllConfigs() {
        return this.sysConfigPoMapper.getAllConfigs();
    }

    @Cacheable(key = "'sysConfig_type:'+#type + 'value:'+#value", value = CacheManagerConfig.EhCacheNames.CACHE_10MINS, cacheManager =
            CacheManagerConfig.CacheManagerNames.EHCACHE_CACHE_MANAGER)
    public SysConfigPo queryByTypeValue(Integer type, Integer value) {
        return this.sysConfigPoMapper.queryByTypeValue(type, value);
    }


    public Map<Integer,String> getTypeMap(int type){
        return self.getAllConfigs().stream().filter(e ->type==e.getType())
                .collect(Collectors.toMap(SysConfigPo::getValue, SysConfigPo::getDescription));
    }


    //刷新缓存
    private void refreshCache(){

    }


    public int insertSelective(SysConfigPo record) {
        return sysConfigPoMapper.insertSelective(record);
    }


    public SysConfigPo selectByPrimaryKey(Long id) {
        return sysConfigPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(SysConfigPo record) {
        return sysConfigPoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<SysConfigPo> list) {
        return sysConfigPoMapper.updateBatch(list);
    }


    public int updateBatchSelective(List<SysConfigPo> list) {
        return sysConfigPoMapper.updateBatchSelective(list);
    }


    public int batchInsert(List<SysConfigPo> list) {
        return sysConfigPoMapper.batchInsert(list);
    }




}
