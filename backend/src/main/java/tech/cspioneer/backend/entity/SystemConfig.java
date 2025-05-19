package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 系统配置表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 配置键
     */
    private String configKey;
    
    /**
     * 配置值
     */
    private String configValue;
    
    /**
     * 配置描述
     */
    private String description;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 