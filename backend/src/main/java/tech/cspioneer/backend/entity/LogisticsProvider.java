package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 物流商表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticsProvider {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 物流商名称（如顺丰、菜鸟）
     */
    private String name;
    
    /**
     * API对接配置（JSON格式）
     */
    private String apiConfig;
} 