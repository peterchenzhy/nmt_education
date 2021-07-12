package com.nmt.education.service.authorization.grade;

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
public class GradeAuthorizationService {

    @Resource
    private GradeAuthorizationPoMapper gradeAuthorizationPoMapper;

    @Autowired
    @Lazy
    private GradeAuthorizationService self;

    /**
     * 根据 登录人获取 年级权限
     *
     * @param userId
     * @return java.util.List<java.lang.Integer>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/4 21:04
     */
    @Cacheable(key = "'gradeAuthorizationService:getGradeAuthorization:userId:'+#userId", value =
            CacheManagerConfig.EhCacheNames.CACHE_10MINS, cacheManager =
            CacheManagerConfig.CacheManagerNames.EHCACHE_CACHE_MANAGER)
    public List<Integer> getGradeAuthorization(Integer userId) {
        Assert.isTrue(userId != null, "用户id不正确");
        List<CampusAuthorizationPo> poList = getGradeAuthorizationByUserId(userId);
        Assert.isTrue(!CollectionUtils.isEmpty(poList), "没有年级系统权限，请联系管理员");
        return poList.stream().map(e -> e.getCode()).collect(Collectors.toList());
    }

    /**
     * 判断 是否有年级权限
     *
     * @param userId
     * @param grade
     * @return java.util.List<java.lang.Integer>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/4 21:16
     */
    public List<Integer> getGradeAuthorization(Integer userId, Integer grade) {
        Assert.isTrue(userId != null, "用户id不正确");
        List<Integer> campusList = self.getGradeAuthorization(userId);
        if(Objects.nonNull(grade)){
            Assert.isTrue(campusList.contains(grade), "没有系统权限（年级），请联系管理员");
            return Lists.newArrayList(grade);
        }else{
            return campusList;
        }

    }

    private List<CampusAuthorizationPo> getGradeAuthorizationByUserId(Integer userId) {
        return this.gradeAuthorizationPoMapper.getGradeAuthorizationByUserId(userId);
    }


}
