package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.ecommerce.config.AppConstants;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.responseDTO.CategoryDTO;
import com.example.ecommerce.responseDTO.CategoryResponse;
import com.example.ecommerce.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("public/categories")
    public ResponseEntity<CategoryResponse> GetCatgories(
        @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
        @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
        @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_BY_Category, required = false) String sortBy,
        @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    )
    { 
        // 1 wayy of using the status code
        return ResponseEntity.ok(categoryService.getAllCategories(pageNumber, pageSize, sortBy,sortOrder));
    }

    @PostMapping("admin/categories")
    public ResponseEntity<String> createCategory(@Valid @RequestBody CategoryDTO category)
    { 
        try{
        String categoryState = categoryService.CreateCategories(category);
        // 2 way of using the status code
        return new ResponseEntity<>(categoryState, HttpStatus.CREATED); 
        }
        catch(ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        } 
    }

    @DeleteMapping("admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId)
    {
        try{
            categoryService.deleteCategory(categoryId);
            // 3 wayy of using the status code
            return ResponseEntity.status(HttpStatus.OK).body("Category of id "+categoryId+ " is removed");
        }
        catch(ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
        
    }

    @PutMapping("admin/categories/{categoryId}")
    public ResponseEntity<String> UpdateCategory(@RequestBody CategoryDTO category,@PathVariable("categoryId") Long categoryId)
    {
         try{
            return ResponseEntity.ok(categoryService.UpdateCategory(category,categoryId)); 
        }
        catch(ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        } 
    }
}
