package com.example.ecommerce.service;

import java.util.List;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.responseDTO.CategoryDTO;
import com.example.ecommerce.responseDTO.CategoryResponse; 

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) { 
        Sort sort = sortOrder.equalsIgnoreCase("asc") 
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
        Pageable pageDetails=PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> categoryPages=categoryRepository.findAll(pageDetails);
        List<Category> categories =  categoryPages.getContent();
        List<CategoryDTO> categoriesDTO = categories.stream()
                                        .map(category -> modelMapper.map(category, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoriesDTO);
        categoryResponse.setPageNumber(categoryPages.getNumber());
        categoryResponse.setPageSize(categoryPages.getSize());
        categoryResponse.setTotalElements(categoryPages.getTotalElements());
        categoryResponse.setTotalPages(categoryPages.getTotalPages());
        categoryResponse.setLastPage(categoryPages.isLast());
        return categoryResponse;            
    }

    public String CreateCategories(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO,Category.class);
        Category saveCategory=categoryRepository.findByCategoryName(category.getCategoryName());
        if(saveCategory !=null )
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already a category "+category.getCategoryName()+" of this name exist");
        }
        categoryRepository.save(category);
        return "category  saved";
    }

    public String deleteCategory(Long categoryId) {
        Optional<Category> category=categoryRepository.findById(categoryId);
        if(category.isPresent())
        {
            categoryRepository.delete(category.get());
            return "removed category item successfully";
        }
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "With id "+categoryId+ " no category present");
    }

    public String UpdateCategory(CategoryDTO categoryDTO, Long categoryId) { 
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category safeCategory=categoryRepository.findByCategoryName(category.getCategoryName());
        if(safeCategory !=null )
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already a category "+category.getCategoryName()+" of this name exist");
        }
        Optional<Category> saveCategory=categoryRepository.findById(categoryId);
        if(saveCategory.isPresent())
        {
            Category flag=saveCategory.get();
            flag.setCategoryName(category.getCategoryName()); 
            categoryRepository.save(flag);
            return "Updated category item";
        }
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "With id "+categoryId+ " no category present");
    }
    
}
