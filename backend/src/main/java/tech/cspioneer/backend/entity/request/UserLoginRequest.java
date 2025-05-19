package tech.cspioneer.backend.entity.request;

import lombok.Data;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest {
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 密码
     */
    private String password;
} 

