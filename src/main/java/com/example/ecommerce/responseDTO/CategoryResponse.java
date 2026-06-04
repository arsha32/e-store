package com.example.ecommerce.responseDTO;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor; 

@Data
@NoArgsConstructor 
public class CategoryResponse {
    private List<CategoryDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
