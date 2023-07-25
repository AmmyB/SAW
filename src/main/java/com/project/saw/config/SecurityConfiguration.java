package com.project.saw.config;


import com.project.saw.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    private UserService userService;

    public SecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.PATCH, "/event/{eventId}").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.DELETE, "/event/{eventId}").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.POST, "/event").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.GET, "/user").hasRole("ORGANIZER")
                .requestMatchers("/event").permitAll()
                .requestMatchers("/event/search").permitAll()
                .anyRequest().authenticated();
        return httpSecurity.build();
    }


    protected UserDetailsService userDetailsService() {
        return userService;
    }

}
