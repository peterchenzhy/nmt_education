package com.nmt.education.service.campus.authorization;

import com.google.common.collect.Lists;
import com.nmt.education.config.cache.CacheManagerConfig;
import com.nmt.education.pojo.po.CampusAuthorizationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CampusAuthorizationService {

    @Resource
    private CampusAuthorizationPoMapper campusAuthorizationPoMapper;

    @Autowired
    @Lazy
    private CampusAuthorizationService self;

    /**
     * 根据 登录人获取 校区权限
     *
     * @param userId
     * @return java.util.List<java.lang.Integer>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/4 21:04
     */
    @Cacheable(key = "'campusAuthorizationService:getCampusAuthorization:userId:'+#userId", value =
            CacheManagerConfig.EhCacheNames.CACHE_10MINS, cacheManager =
            CacheManagerConfig.CacheManagerNames.EHCACHE_CACHE_MANAGER)
    public List<Integer> getCampusAuthorization(Integer userId) {
        Assert.isTrue(userId != null, "用户id不正确");
        List<CampusAuthorizationPo> poList = getCampusAuthorizationByUserId(userId);
        Assert.isTrue(!CollectionUtils.isEmpty(poList), "没有系统权限，请联系管理员");
        return poList.stream().map(e -> e.getCode()).collect(Collectors.toList());
    }

    /**
     * 判断 是否有校区权限
     *
     * @param userId
     * @param campus
     * @return java.util.List<java.lang.Integer>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/4 21:16
     */
    public List<Integer> getCampusAuthorization(Integer userId, Integer campus) {
        Assert.isTrue(userId != null, "用户id不正确");
        List<Integer> campusList = self.getCampusAuthorization(userId);
        if(Objects.nonNull(campus)){
            Assert.isTrue(campusList.contains(campus), "没有系统权限，请联系管理员");
            return Lists.newArrayList(campus);
        }else{
            return campusList;
        }

    }

    private List<CampusAuthorizationPo> getCampusAuthorizationByUserId(Integer userId) {
        return this.campusAuthorizationPoMapper.getCampusAuthorizationByUserId(userId);
    }


    public int insertSelective(CampusAuthorizationPo record) {
        return campusAuthorizationPoMapper.insertSelective(record);
    }


    public CampusAuthorizationPo selectByPrimaryKey(Long id) {
        return campusAuthorizationPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(CampusAuthorizationPo record) {
        return campusAuthorizationPoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatchSelective(List<CampusAuthorizationPo> list) {
        return campusAuthorizationPoMapper.updateBatchSelective(list);
    }


    public int batchInsert(List<CampusAuthorizationPo> list) {
        return campusAuthorizationPoMapper.batchInsert(list);
    }

}
