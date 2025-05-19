package tech.cspioneer.backend.entity.request;

import lombok.Data;

@Data
public class GenerateVerifyCodeRequest {
    /**
     * 邮箱
     */
    private String email;
    //先只支持邮箱
}