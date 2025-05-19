package tech.cspioneer.backend.entity.request;
import java.math.BigDecimal;

import lombok.Data;
@Data
public class ProductRequest {
   
    private long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String status;
    private String imageUrl;
   
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;
    private Integer availableStock;
    private Integer lockedStock;
    private Integer lowStockThreshold;
}
