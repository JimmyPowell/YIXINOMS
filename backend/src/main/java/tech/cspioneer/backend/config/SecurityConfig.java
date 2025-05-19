package tech.cspioneer.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置Spring Security过滤链
     * 初始阶段放行所有端口
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 禁用CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        // 允许所有请求
                        .anyRequest().permitAll()
                )
                // 构建
                .build();
    }

    /**
     * 密码编码器
     * 使用BCrypt加密算法
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 