package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

/**
 * 订单明细表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 关联订单ID
     */
    private Long orderId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称（冗余字段）
     */
    private String productName;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 单价
     */
    private BigDecimal price;
    
    /**
     * 小计金额
     */
    private BigDecimal subtotal;
} 