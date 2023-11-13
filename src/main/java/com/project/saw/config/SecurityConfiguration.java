package com.project.saw.config;


import com.project.saw.user.UserRole;
import com.project.saw.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserService userService;

    @Autowired
    public SecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests()
                .requestMatchers(HttpMethod.PATCH, "/event/{eventId}").hasAuthority(UserRole.ORGANIZER.name())
                .requestMatchers(HttpMethod.DELETE, "/event/{eventId}").hasAuthority(UserRole.ORGANIZER.name())
                .requestMatchers(HttpMethod.POST, "/event").hasAuthority(UserRole.ORGANIZER.name())
                .requestMatchers(HttpMethod.GET, "/user").hasAuthority(UserRole.ORGANIZER.name())
                .requestMatchers("/event").permitAll()
                .requestMatchers("/event/search").permitAll()
                .anyRequest().permitAll()
                .and()
                .httpBasic().and().formLogin().and().logout()
                .and()
                .csrf().disable()
                .headers().frameOptions().disable();

        return httpSecurity.build();
    }


    protected UserDetailsService userDetailsService() {
        return userService;
    }
}

