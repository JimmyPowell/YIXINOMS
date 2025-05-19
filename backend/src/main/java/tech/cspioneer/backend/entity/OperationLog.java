package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 操作日志表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationLog {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 操作类型（order_edit, payment_refund）
     */
    private String operationType;
    
    /**
     * 操作描述
     */
    private String description;
    
    /**
     * 操作IP
     */
    private String ipAddress;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}