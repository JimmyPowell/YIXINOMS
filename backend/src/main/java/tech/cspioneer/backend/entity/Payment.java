package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 关联订单ID
     */
    private Long orderId;
    
    /**
     * 支付用户ID
     */
    private Long userId;
    
    /**
     * 支付金额
     */
    private BigDecimal amount;
    
    /**
     * 支付方式（wechat, alipay, bank）
     */
    private String paymentMethod;
    
    /**
     * 第三方交易ID
     */
    private String transactionId;
    
    /**
     * 支付状态（pending, success, failed, refunded）
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 