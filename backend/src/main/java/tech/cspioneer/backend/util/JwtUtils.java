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

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 根据用户信息生成JWT Token
     *
     * @param uuid    用户唯一标识
     * @param roles   用户角色列表
     * @param claims  其他载荷数据
     * @return JWT Token字符串
     */
    public String generateToken(String uuid, List<String> roles, Map<String, Object> claims) {
        Map<String, Object> allClaims = new HashMap<>();
        if (claims != null) {
            allClaims.putAll(claims);
        }
        allClaims.put("uuid", uuid);
        allClaims.put("roles", roles);

        return Jwts.builder()
                .setClaims(allClaims)
                .setSubject(uuid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成JWT Token（简化版，只包含uuid和角色信息）
     *
     * @param uuid  用户唯一标识
     * @param roles 用户角色列表
     * @return JWT Token字符串
     */
    public String generateToken(String uuid, List<String> roles) {
        return generateToken(uuid, roles, null);
    }

    /**
     * 获取签名密钥
     *
     * @return 密钥对象
     */
    private SecretKey getSignKey() {
        // 确保secret的字节长度至少为256位（32字节）
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从Token中提取Claims
     *
     * @param token JWT Token
     * @return Claims对象
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从Token中提取特定数据
     *
     * @param token JWT Token
     * @param claimsResolver Claims解析函数
     * @param <T> 返回类型
     * @return 解析结果
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从Token中提取用户UUID
     *
     * @param token JWT Token
     * @return 用户UUID
     */
    public String extractUuid(String token) {
        return extractClaim(token, claims -> claims.get("uuid", String.class));
    }

    /**
     * 从Token中提取用户角色列表
     *
     * @param token JWT Token
     * @return 用户角色列表
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    /**
     * 提取Token的过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 检查Token是否已过期
     *
     * @param token JWT Token
     * @return 是否已过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        try {
            // 验证签名和过期时间
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            
            return !isTokenExpired(token);
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
     * 验证Token并返回解析结果
     * 
     * @param token JWT Token
     * @return 解析结果，包含token是否有效、用户UUID和角色列表
     */
    public TokenValidationResult validateAndExtractInfo(String token) {
        TokenValidationResult result = new TokenValidationResult();
        
        try {
            // 验证Token并提取信息
            Claims claims = extractAllClaims(token);
            
            // 检查是否过期
            if (claims.getExpiration().before(new Date())) {
                result.setValid(false);
                result.setErrorMessage("Token已过期");
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