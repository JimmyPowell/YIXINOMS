package tech.cspioneer.backend.entity.request;

import lombok.Data;

/**
 * 刷新令牌请求
 */
@Data
public class RefreshTokenRequest {
    /**
     * 刷新令牌
     */
    private String refreshToken;
} 