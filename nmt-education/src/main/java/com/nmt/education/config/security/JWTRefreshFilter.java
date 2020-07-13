package com.nmt.education.config.security;

import com.nmt.education.commmons.Consts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 替换掉UsernamePasswordAuthenticationFilter ，构成一个NmtAuthenticationToken
 */
public class JWTRefreshFilter extends BasicAuthenticationFilter {


    public JWTRefreshFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            NmtAuthenticationToken token = (NmtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            response.setHeader(Consts.NMT_TOKEN_HEAD, token.getToken());
        }
        chain.doFilter(request,response);
    }


}
