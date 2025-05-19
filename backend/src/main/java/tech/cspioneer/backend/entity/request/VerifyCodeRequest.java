package tech.cspioneer.backend.entity.request;

import lombok.Data;

@Data
public class VerifyCodeRequest {
    /**
     * 会话ID
     */
    private String sessionId;
    /**
     * 验证码
     */
    private String code;
}
