package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 第三方登录关联表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserThirdParty {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 关联用户ID
     */
    private Long userId;
    
    /**
     * 平台类型（wechat, github等）
     */
    private String platform;
    
    /**
     * 第三方平台用户唯一标识
     */
    private String openId;
    
    /**
     * 第三方平台访问令牌（可选）
     */
    private String accessToken;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 