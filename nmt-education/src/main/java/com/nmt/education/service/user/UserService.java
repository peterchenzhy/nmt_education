package com.nmt.education.service.user;

import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.utils.TokenUtil;
import com.nmt.education.pojo.dto.req.UserLoginDto;
import com.nmt.education.pojo.po.UserPo;
import com.nmt.education.pojo.vo.UserVo;
import com.nmt.education.service.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UserService {

    @Resource
    private UserPoMapper userPoMapper;
    @Resource
    private AuthorizationService authorizationService;

    /**
     * 登录
     *
      * @param dto
     * @return   com.nmt.education.pojo.vo.UserVo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/2 23:47
     */
    public UserVo login(UserLoginDto dto) {
        UserPo userPo = this.userPoMapper.queryByLoginDto(dto);
        Assert.notNull(userPo,"登录失败，用户："+dto.getCode());
        //check 校区权限 及 年级权限
        authorizationService.getAuthorization(userPo.getCode());
        UserVo vo = new UserVo(userPo);
        TokenUtil.Token token = new TokenUtil.Token(vo.getLogInUser(),vo.getRoleId());
        vo.setToken(TokenUtil.generateToken(token));
        log.info("用户登录成功,id:[{}],code:[{}] ,姓名:[{}]",userPo.getId(),userPo.getCode(),userPo.getName());

        return vo ;
    }



    public int insertSelective(UserPo record) {
        return userPoMapper.insertSelective(record);
    }

    
    public UserPo selectByPrimaryKey(Long id) {
        return userPoMapper.selectByPrimaryKey(id);
    }

    
    public int updateByPrimaryKeySelective(UserPo record) {
        return userPoMapper.updateByPrimaryKeySelective(record);
    }


    public String updatePassword(Map<String, String> map) {
        String key = map.get("key");
        Assert.isTrue(StringUtils.hasLength(key), "秘钥不能为空");
        Assert.isTrue("13774493070".equals(key),"秘钥不正确");
        String id = map.get("id");
        Assert.isTrue(StringUtils.hasLength(id), "用户不能为空");
        String password = map.get("password");
        Assert.isTrue(StringUtils.hasLength(password), "密码不能为空");
        UserPo userPo = selectByPrimaryKey(Long.valueOf(id));
        Assert.isTrue(Objects.nonNull(userPo), "用户信息不能为空");
        userPo.setPassword(encode(password));
        userPo.setOperateTime(new Date());
        userPo.setOperator(Consts.SYSTEM_USER);
        updateByPrimaryKeySelective(userPo);
        return "处理成功";
    }
    private String encode(String str1){
        String str = Base64Utils.encodeToString(str1.getBytes());
        String md51 = DigestUtils.md5Hex(DigestUtils.md5Hex(str));
       return md51;
    }
}
