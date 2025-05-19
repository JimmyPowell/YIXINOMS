package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 售后处理记录表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AfterSalesLog {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 售后申请ID
     */
    private Long applicationId;
    
    /**
     * 操作人（客服或系统）
     */
    private String operator;
    
    /**
     * 操作类型（approve, reject, refund_processed）
     */
    private String action;
    
    /**
     * 操作备注
     */
    private String note;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
} 