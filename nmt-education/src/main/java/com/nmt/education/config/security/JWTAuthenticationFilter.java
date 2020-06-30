package com.nmt.education.config.security;

import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.utils.TokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static com.nmt.education.commmons.Consts.NMT_TOKEN_HEAD;

/**
 * 替换掉UsernamePasswordAuthenticationFilter ，构成一个NmtAuthenticationToken
 */
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request.getRequestURI().equals("/nmt-education/user/login")){
            NmtAuthenticationToken tk = new NmtAuthenticationToken(Consts.LOGIN_USER_HEAD,Consts.ROLE_ROOT,"",  Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(tk);
            //进入下一个filter
            chain.doFilter(request,response);
        }else {
            String token = request.getHeader(NMT_TOKEN_HEAD);
            String roleId = request.getHeader(Consts.ROLE_ID_HEAD);
            Integer loginUserId = Integer.valueOf(request.getHeader(Consts.LOGIN_USER_HEAD));
            //构造一个临时的 token
            NmtAuthenticationToken tk = new NmtAuthenticationToken(loginUserId, roleId, token, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(tk);

            //进入下一个filter
            chain.doFilter(request, response);
        }
    }


}
