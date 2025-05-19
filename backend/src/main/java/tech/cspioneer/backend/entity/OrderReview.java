package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 订单评价表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderReview {
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
     * 评分（1-5）
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String comment;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 