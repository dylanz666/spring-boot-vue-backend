package com.github.dylanz666.config;

import com.github.dylanz666.constant.UserTypeEnum;
import com.github.dylanz666.domain.AuthorizationException;
import com.github.dylanz666.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthorizationException authorizationException;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable().cors().and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest)
                .permitAll()
                .antMatchers("/", "/api/ping", "/api/login").permitAll()//这3个url不用访问认证
                .antMatchers("/admin/**").hasRole(UserTypeEnum.ADMIN.toString())
                .antMatchers("/user/**").hasRole(UserTypeEnum.USER.toString())
                .anyRequest()
                .authenticated()//其他url都需要访问认证
                .and()
                .formLogin()
                .loginPage("/login")//登录页面的url
                .loginProcessingUrl("/login")//登录表使用的API
                .permitAll()//login.html和login不需要访问认证
                .and()
                .logout()
                .permitAll()//logout不需要访问认证
                .and()
                .exceptionHandling()
                .accessDeniedHandler(((httpServletRequest, httpServletResponse, e) -> {
                    e.printStackTrace();
                    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    httpServletResponse.setContentType("application/json");
                    authorizationException.setCode(HttpServletResponse.SC_FORBIDDEN);
                    authorizationException.setStatus("FAIL");
                    authorizationException.setMessage("FORBIDDEN");
                    authorizationException.setUri(httpServletRequest.getRequestURI());
                    PrintWriter printWriter = httpServletResponse.getWriter();
                    printWriter.write(authorizationException.toString());
                    printWriter.flush();
                    printWriter.close();
                }))
                .authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> {
                    /*if (httpServletRequest.getRequestURI().equals("/hello.html")) {
                        httpServletResponse.sendRedirect("/login.html");
                        return;
                    }
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpServletResponse.setContentType("application/json");
                    authorizationException.setCode(HttpServletResponse.SC_UNAUTHORIZED);
                    authorizationException.setStatus("FAIL");
                    authorizationException.setUri(httpServletRequest.getRequestURI());
                    authorizationException.setMessage("UNAUTHORIZED");
                    PrintWriter printWriter = httpServletResponse.getWriter();
                    printWriter.write(authorizationException.toString());
                    printWriter.flush();
                    printWriter.close();*/
                });
        httpSecurity.userDetailsService(userDetailsService());
        httpSecurity.userDetailsService(userDetailsService);
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        UserDetails dylanz =
                User.withUsername("dylanz")
                        .password(bCryptPasswordEncoder.encode("666"))
                        .roles(UserTypeEnum.ADMIN.toString())
                        .build();
        UserDetails ritay =
                User.withUsername("ritay")
                        .password(bCryptPasswordEncoder.encode("888"))
                        .roles(UserTypeEnum.USER.toString())
                        .build();
        UserDetails jonathanw =
                User.withUsername("jonathanw")
                        .password(bCryptPasswordEncoder.encode("999"))
                        .roles(UserTypeEnum.USER.toString())
                        .build();
        return new InMemoryUserDetailsManager(dylanz, ritay, jonathanw);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_" + UserTypeEnum.ADMIN.toString() + " > ROLE_" + UserTypeEnum.USER.toString());
        return roleHierarchy;
    }
}
