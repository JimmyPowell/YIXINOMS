package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 售后申请表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AfterSalesApplication {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 类型（return, exchange）
     */
    private String type;
    
    /**
     * 申请原因
     */
    private String reason;
    
    /**
     * 处理状态（pending, approved, rejected, completed）
     */
    private String status;
    
    /**
     * 退货物流单号
     */
    private String logisticsNumber;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 