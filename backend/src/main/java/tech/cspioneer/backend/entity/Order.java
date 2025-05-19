package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 下单用户ID
     */
    private Long userId;
    
    /**
     * 订单编号（自动生成）
     */
    private String orderNumber;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态（pending, paid, shipped, completed, canceled）
     */
    private String status;
    
    /**
     * 支付方式（wechat, alipay, bank_transfer）
     */
    private String paymentMethod;
    
    /**
     * 收货地址
     */
    private String shippingAddress;
    
    /**
     * 配送方式（express, pickup_in_store）
     */
    private String shippingMethod;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 