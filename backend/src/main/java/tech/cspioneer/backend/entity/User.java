package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 用户表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户唯一标识符
     */
    private String uuid;
    
    /**
     * 用户名（可为空，用于账号密码登录）
     */
    private String userName;
    
    /**
     * 邮箱（可为空，用于账号密码登录）
     */
    private String email;
    
    /**
     * 手机号（绑定后必填）
     */
    private String phone;
    
    /**
     * 加密后的密码（账号密码登录时使用）
     */
    private String password;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 性别（0-未知 1-男 2-女）
     */
    private Integer gender;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 注册时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除（0-未删除 1-已删除）
     */
    private Integer isDeleted;

    
    
} 