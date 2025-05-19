package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 订单状态历史表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistory {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 旧状态
     */
    private String oldStatus;
    
    /**
     * 新状态
     */
    private String newStatus;
    
    /**
     * 状态变更时间
     */
    private LocalDateTime changedAt;
    
    /**
     * 操作人（用户或系统）
     */
    private String operator;
} 