package com.nmt.education.config.security.login;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录filter
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    /**
     * 登录
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = request.getParameter("user") == null ? "" : request.getParameter("user").trim();
        String password = request.getParameter("password") == null ? "" : request.getParameter("password");


        return super.attemptAuthentication(request, response);
    }


    /**
     * 成功登录 回调
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/31 22:33
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
    }

    /**
     * 登录失败回调
     *
     * @param request
     * @param response
     * @param failed
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/5/31 22:33
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
