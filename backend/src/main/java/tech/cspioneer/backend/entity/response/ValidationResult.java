package tech.cspioneer.backend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数验证结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    
    /**
     * 是否验证通过
     */
    private boolean valid;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 错误码
     */
    private String code;
    
    /**
     * 附加数据
     */
    private Object data;
    
    /**
     * 创建成功结果
     * @return 成功结果
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null, null, null);
    }
    
    /**
     * 创建带数据的成功结果
     * @param data 数据
     * @return 成功结果
     */
    public static ValidationResult success(Object data) {
        return new ValidationResult(true, null, null, data);
    }
    
    /**
     * 创建失败结果
     * @param message 错误消息
     * @return 失败结果
     */
    public static ValidationResult fail(String message) {
        return new ValidationResult(false, message, "400", null);
    }
    
    /**
     * 创建失败结果
     * @param message 错误消息
     * @param code 错误码
     * @return 失败结果
     */
    public static ValidationResult fail(String message, String code) {
        return new ValidationResult(false, message, code, null);
    }
} 