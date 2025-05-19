package tech.cspioneer.backend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话响应实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    /**
     * 会话ID
     */
    private String session;
} 