package com.override.orchestrator_service.config;

import com.override.orchestrator_service.config.filter.AdminPageFilter;
import com.override.orchestrator_service.config.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private AdminPageFilter adminPageFilter;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();

        httpSecurity.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/templates/**", "/scripts/**", "/css/**").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/transaction", "/voice_message", "/register").permitAll()
                .antMatchers("/categories/**").permitAll()
                .antMatchers("/admin/**").authenticated()
                .antMatchers("/announce/**").authenticated()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login")
                .and().addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class).addFilterAfter(adminPageFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
