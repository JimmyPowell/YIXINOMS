package tech.cspioneer.backend.entity.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponseForClient {
   
    private String category;
    private String name;
    private String description;
    private BigDecimal price;
    private String status;
    private String imageUrl;
    private String uuid;

}
