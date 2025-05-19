package tech.cspioneer.backend.entity.request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    /**
     * 用户名
     */     
    private String userName;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号
     */
    private String phone;
    
}
