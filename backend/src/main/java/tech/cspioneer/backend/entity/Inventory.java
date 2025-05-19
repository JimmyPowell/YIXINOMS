package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 商品库存表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称（冗余字段）
     */
    private String productName;
    
    /**
     * 可用库存
     */
    private Integer availableStock;
    
    /**
     * 锁定库存（已下单但未发货）
     */
    private Integer lockedStock;
    
    /**
     * 库存预警阈值
     */
    private Integer lowStockThreshold;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 