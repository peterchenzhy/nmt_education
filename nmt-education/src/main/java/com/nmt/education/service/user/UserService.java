package com.nmt.education.service.user;

import com.nmt.education.commmons.utils.TokenUtil;
import com.nmt.education.pojo.dto.req.UserLoginDto;
import com.nmt.education.pojo.po.UserPo;
import com.nmt.education.pojo.vo.UserVo;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserPoMapper userPoMapper;

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
        UserVo vo = new UserVo(userPo);
        TokenUtil.Token token = new TokenUtil.Token(vo.getLogInUser(),vo.getRoleId());

        vo.setToken(TokenUtil.generateToken(token));
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



}
