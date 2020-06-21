package com.nmt.education.config.security;

import com.nmt.education.config.security.login.LoginAuthicationProvider;
import com.nmt.education.config.security.login.LoginUsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginAuthicationProvider loginAuthicationProvider;

    public static final String LOGIN_PAGE = "/login.html";
    private final String LOGIN_URL = "/user/login";
    private final String LOGOUT_URL = "/user/logout";
    private final String INDEX_URL = "/index";

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/**/**swagger**/**", "/v2/api-docs",LOGIN_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginProcessingUrl(LOGIN_URL)
                .and()
                .addFilterAt(getFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic()
//                .addFilter(new LoginAuthicationProvider())
                .and()
                .logout()
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessUrl(LOGIN_PAGE)
                .deleteCookies().clearAuthentication(true).invalidateHttpSession(true)
                .permitAll()
                .and()
                .csrf().disable();

    }



    @Bean
    public LoginUsernamePasswordAuthenticationFilter getFilter() throws Exception {
        LoginUsernamePasswordAuthenticationFilter filter =  new LoginUsernamePasswordAuthenticationFilter(this.LOGIN_URL);
        filter.setAuthenticationManager(super.authenticationManagerBean());
        return filter;
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
