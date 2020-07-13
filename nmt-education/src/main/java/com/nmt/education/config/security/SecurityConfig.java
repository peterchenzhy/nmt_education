package com.nmt.education.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String LOGIN_PAGE = "/login.html";
    private final String LOGIN_URL = "/user/login";
    private final String LOGOUT_URL = "/user/logout";
    private final String INDEX_URL = "/index";

    @Value("${spring.profiles.active}")
    private String env ;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/**/**swagger**/**", "/v2/api-docs", LOGIN_URL)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterAt(getJWTAuthenticationFilter(authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(getJWTRefreshFilter(authenticationManagerBean()), FilterSecurityInterceptor.class)
                .httpBasic()
                .authenticationEntryPoint(new NmtAuthenticationCommence())
                .and()
                .logout()
                .logoutUrl(LOGOUT_URL)
                .deleteCookies().clearAuthentication(true).invalidateHttpSession(true)
                .permitAll()
                .and()
                .csrf().disable()
                .anonymous().disable();

    }

    private JWTAuthenticationFilter getJWTAuthenticationFilter(AuthenticationManager manager) throws Exception {
        JWTAuthenticationFilter filter = new JWTAuthenticationFilter(manager);
        return filter;
    }
    private JWTRefreshFilter getJWTRefreshFilter(AuthenticationManager manager) throws Exception {
        JWTRefreshFilter filter = new JWTRefreshFilter(manager);
        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new NmtAuthenticationTokenProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "images/**");
    }

}
