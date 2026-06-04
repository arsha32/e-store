package com.example.ecommerce.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long productId;
    // private CartDTO cartDTO;
    // private ProductDTO productDTO;
    private Integer quantity;
    // private Double discount;
    // private Double productPrice;
}
