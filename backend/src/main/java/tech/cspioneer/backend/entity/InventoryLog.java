package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 库存变动记录表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryLog {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 变动类型（inbound, outbound, cancel）
     */
    private String changeType;
    
    /**
     * 变动数量
     */
    private Integer quantity;
    
    /**
     * 关联ID（如订单ID或采购单ID）
     */
    private Long relatedId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 