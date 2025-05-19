package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 支付ID
     */
    private Long paymentId;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退款原因
     */
    private String reason;
    
    /**
     * 退款状态（processing, success, failed）
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 