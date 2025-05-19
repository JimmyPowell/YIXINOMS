package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 用户优惠券领取记录表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCoupon {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 是否已使用（0-未使用 1-已使用）
     */
    private Integer used;
    
    /**
     * 使用时间
     */
    private LocalDateTime usedAt;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 