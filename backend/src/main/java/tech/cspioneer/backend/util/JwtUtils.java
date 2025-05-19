package tech.cspioneer.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;

/**
 * JWT工具类，用于生成和解析JWT Token
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Access Token配置
    @Value("${jwt.access-token.secret}")
    private String accessTokenSecret;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    // Refresh Token配置
    @Value("${jwt.refresh-token.secret}")
    private String refreshTokenSecret;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    /**
     * 生成Access Token
     *
     * @param uuid    用户唯一标识
     * @param roles   用户角色列表
     * @param claims  其他载荷数据
     * @return JWT Access Token字符串
     */
    public String generateAccessToken(String uuid, List<String> roles, Map<String, Object> claims) {
        Map<String, Object> allClaims = new HashMap<>();
        if (claims != null) {
            allClaims.putAll(claims);
        }
        allClaims.put("uuid", uuid);
        allClaims.put("roles", roles);
        allClaims.put("token_type", "access");

        return Jwts.builder()
                .setClaims(allClaims)
                .setSubject(uuid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getAccessTokenSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成Access Token（简化版，只包含uuid和角色信息）
     *
     * @param uuid  用户唯一标识
     * @param roles 用户角色列表
     * @return JWT Access Token字符串
     */
    public String generateAccessToken(String uuid, List<String> roles) {
        return generateAccessToken(uuid, roles, null);
    }
    
    /**
     * 生成Refresh Token
     *
     * @param uuid 用户唯一标识
     * @return JWT Refresh Token字符串
     */
    public String generateRefreshToken(String uuid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uuid", uuid);
        claims.put("token_type", "refresh");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(uuid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getRefreshTokenSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 获取Access Token签名密钥
     *
     * @return 密钥对象
     */
    private SecretKey getAccessTokenSignKey() {
        byte[] keyBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 获取Refresh Token签名密钥
     *
     * @return 密钥对象
     */
    private SecretKey getRefreshTokenSignKey() {
        byte[] keyBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从Access Token中提取Claims
     *
     * @param token JWT Token
     * @return Claims对象
     */
    private Claims extractAccessTokenClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getAccessTokenSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 从Refresh Token中提取Claims
     *
     * @param token JWT Token
     * @return Claims对象
     */
    private Claims extractRefreshTokenClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getRefreshTokenSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 根据token类型提取Claims
     * 
     * @param token JWT Token
     * @param isRefreshToken 是否为刷新令牌
     * @return Claims对象
     */
    private Claims extractAllClaims(String token, boolean isRefreshToken) {
        return isRefreshToken ? extractRefreshTokenClaims(token) : extractAccessTokenClaims(token);
    }

    /**
     * 从Token中提取特定数据
     *
     * @param token JWT Token
     * @param claimsResolver Claims解析函数
     * @param isRefreshToken 是否为刷新令牌
     * @param <T> 返回类型
     * @return 解析结果
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, boolean isRefreshToken) {
        final Claims claims = extractAllClaims(token, isRefreshToken);
        return claimsResolver.apply(claims);
    }

    /**
     * 从Token中提取用户UUID
     *
     * @param token JWT Token
     * @param isRefreshToken 是否为刷新令牌
     * @return 用户UUID
     */
    public String extractUuid(String token, boolean isRefreshToken) {
        return extractClaim(token, claims -> claims.get("uuid", String.class), isRefreshToken);
    }

    /**
     * 从Token中提取用户角色列表（只适用于Access Token）
     *
     * @param token JWT Access Token
     * @return 用户角色列表
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class), false);
    }

    /**
     * 提取Token的过期时间
     *
     * @param token JWT Token
     * @param isRefreshToken 是否为刷新令牌
     * @return 过期时间
     */
    public Date extractExpiration(String token, boolean isRefreshToken) {
        return extractClaim(token, Claims::getExpiration, isRefreshToken);
    }

    /**
     * 检查Token是否已过期
     *
     * @param token JWT Token
     * @param isRefreshToken 是否为刷新令牌
     * @return 是否已过期
     */
    private Boolean isTokenExpired(String token, boolean isRefreshToken) {
        final Date expiration = extractExpiration(token, isRefreshToken);
        return expiration.before(new Date());
    }

    /**
     * 验证Access Token是否有效
     *
     * @param token JWT Access Token
     * @return 是否有效
     */
    public Boolean validateAccessToken(String token) {
        try {
            // 验证签名和过期时间
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getAccessTokenSignKey())
                    .build()
                    .parseClaimsJws(token);
            
            Claims claims = claimsJws.getBody();
            String tokenType = claims.get("token_type", String.class);
            
            // 验证token类型
            if (!"access".equals(tokenType)) {
                logger.error("Invalid token type: {}", tokenType);
                return false;
            }
            
            return !isTokenExpired(token, false);
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * 验证Refresh Token是否有效
     *
     * @param token JWT Refresh Token
     * @return 是否有效
     */
    public Boolean validateRefreshToken(String token) {
        try {
            // 验证签名和过期时间
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getRefreshTokenSignKey())
                    .build()
                    .parseClaimsJws(token);
            
            Claims claims = claimsJws.getBody();
            String tokenType = claims.get("token_type", String.class);
            
            // 验证token类型
            if (!"refresh".equals(tokenType)) {
                logger.error("Invalid token type: {}", tokenType);
                return false;
            }
            
            return !isTokenExpired(token, true);
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        
        return false;
    }

    /**
     * 验证Access Token并返回解析结果
     * 
     * @param token JWT Access Token
     * @return 解析结果，包含token是否有效、用户UUID和角色列表
     */
    public TokenValidationResult validateAndExtractAccessTokenInfo(String token) {
        TokenValidationResult result = new TokenValidationResult();
        
        try {
            // 验证Token并提取信息
            Claims claims = extractAccessTokenClaims(token);
            
            // 检查是否过期
            if (claims.getExpiration().before(new Date())) {
                result.setValid(false);
                result.setErrorMessage("Token已过期");
                return result;
            }
            
            // 验证token类型
            String tokenType = claims.get("token_type", String.class);
            if (!"access".equals(tokenType)) {
                result.setValid(false);
                result.setErrorMessage("Token类型错误，不是Access Token");
                return result;
            }
            
            String uuid = claims.get("uuid", String.class);
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            
            result.setValid(true);
            result.setUuid(uuid);
            result.setRoles(roles);
            
        } catch (SignatureException e) {
            result.setValid(false);
            result.setErrorMessage("无效的Token签名");
        } catch (MalformedJwtException e) {
            result.setValid(false);
            result.setErrorMessage("Token格式不正确");
        } catch (ExpiredJwtException e) {
            result.setValid(false);
            result.setErrorMessage("Token已过期");
        } catch (UnsupportedJwtException e) {
            result.setValid(false);
            result.setErrorMessage("不支持的Token");
        } catch (IllegalArgumentException e) {
            result.setValid(false);
            result.setErrorMessage("Token为空");
        } catch (Exception e) {
            result.setValid(false);
            result.setErrorMessage("Token解析异常: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 验证Refresh Token并返回解析结果
     * 
     * @param token JWT Refresh Token
     * @return 解析结果，包含token是否有效、用户UUID
     */
    public TokenValidationResult validateAndExtractRefreshTokenInfo(String token) {
        TokenValidationResult result = new TokenValidationResult();
        
        try {
            // 验证Token并提取信息
            Claims claims = extractRefreshTokenClaims(token);
            
            // 检查是否过期
            if (claims.getExpiration().before(new Date())) {
                result.setValid(false);
                result.setErrorMessage("Refresh Token已过期");
                return result;
            }
            
            // 验证token类型
            String tokenType = claims.get("token_type", String.class);
            if (!"refresh".equals(tokenType)) {
                result.setValid(false);
                result.setErrorMessage("Token类型错误，不是Refresh Token");
                return result;
            }
            
            String uuid = claims.get("uuid", String.class);
            
            result.setValid(true);
            result.setUuid(uuid);
            
        } catch (SignatureException e) {
            result.setValid(false);
            result.setErrorMessage("无效的Token签名");
        } catch (MalformedJwtException e) {
            result.setValid(false);
            result.setErrorMessage("Token格式不正确");
        } catch (ExpiredJwtException e) {
            result.setValid(false);
            result.setErrorMessage("Token已过期");
        } catch (UnsupportedJwtException e) {
            result.setValid(false);
            result.setErrorMessage("不支持的Token");
        } catch (IllegalArgumentException e) {
            result.setValid(false);
            result.setErrorMessage("Token为空");
        } catch (Exception e) {
            result.setValid(false);
            result.setErrorMessage("Token解析异常: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Token验证结果类
     */
    public static class TokenValidationResult {
        private boolean isValid;
        private String uuid;
        private List<String> roles;
        private String errorMessage;
        
        public boolean isValid() {
            return isValid;
        }
        
        public void setValid(boolean valid) {
            isValid = valid;
        }
        
        public String getUuid() {
            return uuid;
        }
        
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
        
        public List<String> getRoles() {
            return roles;
        }
        
        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
} 