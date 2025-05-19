package tech.cspioneer.backend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 访问令牌过期时间（秒）
     */
    private long accessTokenExpiresIn;
    
    /**
     * 刷新令牌过期时间（秒）
     */
    private long refreshTokenExpiresIn;
    
    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";
    
    /**
     * 构造函数（提供默认令牌类型）
     */
    public TokenResponse(String accessToken, String refreshToken, long accessTokenExpiresIn, long refreshTokenExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        this.tokenType = "Bearer";
    }
} 