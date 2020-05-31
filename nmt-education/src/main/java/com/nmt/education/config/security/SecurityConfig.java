package com.nmt.education.config.security;

import com.nmt.education.pojo.dto.req.TeacherReqDto;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String LOGIN_PAGE = "/login.html";
    private  final String LOGIN_URL = "/user/login";
    private  final String LOGOUT_URL = "/user/logout";
    private final String INDEX_URL = "/index";

    @Override
    protected void configure(HttpSecurity http) throws Exception {



        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage(LOGIN_PAGE).loginProcessingUrl(LOGIN_URL)
                .successForwardUrl(INDEX_URL).failureForwardUrl(LOGIN_PAGE)
                .permitAll()
                .and()
                .logout()
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessUrl(LOGIN_PAGE).deleteCookies().clearAuthentication(true).invalidateHttpSession(true)
                .permitAll()
                .and()
                .csrf().disable();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "images/**");
    }
}
