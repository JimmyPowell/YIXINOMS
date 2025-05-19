package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 商品分类表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 父级分类ID（用于多级分类）
     */
    private Long parentId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 