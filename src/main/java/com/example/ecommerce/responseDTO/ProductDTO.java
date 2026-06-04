package com.example.ecommerce.responseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private String productName;
    private String description;
    private String image;
    private Integer quantity;
    private Long price;
    private Double discount;
    private Double specialPrice;
}
