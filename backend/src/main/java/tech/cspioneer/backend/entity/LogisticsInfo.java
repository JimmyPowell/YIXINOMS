package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 物流信息表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticsInfo {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 关联订单ID
     */
    private Long orderId;
    
    /**
     * 物流单号
     */
    private String logisticsNumber;
    
    /**
     * 物流商ID
     */
    private Long providerId;
    
    /**
     * 物流状态（created, shipped, in_transit, delivered）
     */
    private String status;
    
    /**
     * 当前位置
     */
    private String currentLocation;
    
    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedDelivery;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 