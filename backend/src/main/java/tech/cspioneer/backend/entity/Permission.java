package tech.cspioneer.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 权限表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    /**
     * 权限ID
     */
    private Long id;
    
    /**
     * 权限名称（如order_create, inventory_edit）
     */
    private String permissionName;
    
    /**
     * 权限描述
     */
    private String description;
} 