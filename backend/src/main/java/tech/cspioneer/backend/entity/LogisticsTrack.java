package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 物流轨迹表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticsTrack {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 物流信息ID
     */
    private Long logisticsId;
    
    /**
     * 轨迹状态（如已揽件、运输中）
     */
    private String status;
    
    /**
     * 位置信息
     */
    private String location;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
} 