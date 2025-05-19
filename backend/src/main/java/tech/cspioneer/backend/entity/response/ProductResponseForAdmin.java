package tech.cspioneer.backend.entity.response;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductResponseForAdmin {
    private Long id;
    private String category;
    private String name;
    private String description;
    private BigDecimal price;
    private String status;
    private String imageUrl;
    private String uuid;
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;
    private Integer availableStock;
    private Integer lockedStock;
    private Integer lowStockThreshold;
}


