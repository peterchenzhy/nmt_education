package com.nmt.education.service.sysconfig;

import com.nmt.education.config.cache.CacheManagerConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.nmt.education.pojo.po.SysConfigPo;
import com.nmt.education.service.sysconfig.SysConfigPoMapper;
@Service
public class SysConfigService {

    @Resource
    private SysConfigPoMapper sysConfigPoMapper;

    @Cacheable(key = "'getAllConfigs'", value = CacheManagerConfig.EhCacheNames.CACHE_10MINS, cacheManager = CacheManagerConfig.CacheManagerNames.EHCACHE_CACHE_MANAGER)
    public List<SysConfigPo> getAllConfigs(){
        return this.sysConfigPoMapper.getAllConfigs();
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

    
    public int updateByPrimaryKey(SysConfigPo record) {
        return sysConfigPoMapper.updateByPrimaryKey(record);
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

    
    public int insertOrUpdate(SysConfigPo record) {
        return sysConfigPoMapper.insertOrUpdate(record);
    }

    
    public int insertOrUpdateSelective(SysConfigPo record) {
        return sysConfigPoMapper.insertOrUpdateSelective(record);
    }

}
