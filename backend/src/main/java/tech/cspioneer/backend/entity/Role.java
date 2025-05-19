package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 角色表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    /**
     * 角色ID
     */
    private Long id;
    
    /**
     * 角色名称（如admin, customer, merchant）
     */
    private String roleName;
    
    /**
     * 角色描述
     */
    private String description;
} 