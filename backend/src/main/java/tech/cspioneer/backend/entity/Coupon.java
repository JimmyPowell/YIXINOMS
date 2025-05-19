package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 优惠券编码
     */
    private String code;
    
    /**
     * 折扣类型（fixed, percentage）
     */
    private String discountType;
    
    /**
     * 折扣值（如10元或8折）
     */
    private BigDecimal discountValue;
    
    /**
     * 有效期起始时间
     */
    private LocalDateTime validFrom;
    
    /**
     * 有效期结束时间
     */
    private LocalDateTime validTo;
    
    /**
     * 使用限制（每人限用次数）
     */
    private Integer usageLimit;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 