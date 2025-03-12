package com.blog.blog_pg.middleware;

import com.blog.blog_pg.utils.ApiKeyAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AccountAuthFilter accountAuthFilter;
    private final ApiKeyAuthFilter apiKeyAuthFilter;

    public SecurityConfig(AccountAuthFilter accountAuthFilter,ApiKeyAuthFilter apiKeyAuthFilter) {

        this.accountAuthFilter = accountAuthFilter;
        this.apiKeyAuthFilter = apiKeyAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class) // API Key Filter chạy trước xác thực
                .addFilterBefore(accountAuthFilter, UsernamePasswordAuthenticationFilter.class)  // Thêm filter của bạn vào trước filter xác thực
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
