package com.example.ecommerce.controller;

import java.io.IOException;

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
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.config.AppConstants;
import com.example.ecommerce.responseDTO.ProductDTO;
import com.example.ecommerce.responseDTO.ProductResponse;
import com.example.ecommerce.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ProductController{
    
    @Autowired
    private ProductService productService;
    
    @PostMapping("admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> createProduct(@PathVariable Long categoryId, @Valid @RequestBody ProductDTO productDTO)
    { 
        ProductDTO savedProductDTO= productService.createProduct(categoryId, productDTO);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }

    @GetMapping("public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
        @RequestParam(name="category" ,required = false ) String categoryName,
        @RequestParam(name="keyword" ,required = false ) String keyword,
        @RequestParam(name="pageNumber" ,defaultValue = AppConstants.PAGE_NUMBER, required = false ) Integer pageNumber,
        @RequestParam(name="pageSize" ,defaultValue = AppConstants.PAGE_SIZE, required = false ) Integer pageSize,
        @RequestParam(name="sortBy" ,defaultValue = AppConstants.SORT_BY_Product, required = false ) String sortBy,
        @RequestParam(name="sortOrder" ,defaultValue = AppConstants.SORT_ORDER, required = false ) String sortOrder
    )
    {
        ProductResponse productResponse=productService.getALLProducts(pageNumber,pageSize, sortBy, sortOrder,categoryName, keyword);
        return new ResponseEntity<>(productResponse,  HttpStatus.OK);
    }
    
    @GetMapping("public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getAllProductsByCategory(@PathVariable Long categoryId,
        @RequestParam(name="pageNumber" ,defaultValue = AppConstants.PAGE_NUMBER, required = false ) Integer pageNumber,
        @RequestParam(name="pageSize" ,defaultValue = AppConstants.PAGE_SIZE, required = false ) Integer pageSize,
        @RequestParam(name="sortBy" ,defaultValue = AppConstants.SORT_BY_Product, required = false ) String sortBy,
        @RequestParam(name="sortOrder" ,defaultValue = AppConstants.SORT_ORDER, required = false ) String sortOrder
    )
    {
        ProductResponse productResponse=productService.getALLProductsByCategory(categoryId, pageNumber,pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse,  HttpStatus.OK);
    }
    
    @GetMapping("public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getAllProductsByKeyword(@PathVariable String keyword
        ,@RequestParam(name="pageNumber" ,defaultValue = AppConstants.PAGE_NUMBER, required = false ) Integer pageNumber,
        @RequestParam(name="pageSize" ,defaultValue = AppConstants.PAGE_SIZE, required = false ) Integer pageSize,
        @RequestParam(name="sortBy" ,defaultValue = AppConstants.SORT_BY_Product, required = false ) String sortBy,
        @RequestParam(name="sortOrder" ,defaultValue = AppConstants.SORT_ORDER, required = false ) String sortOrder
    )
    {
        ProductResponse productResponse=productService.getALLProductsByKeywordy(keyword , pageNumber,pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse,  HttpStatus.OK);
    }
    
    @PutMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> UpdateProductsById(@PathVariable Long productId
        , @Valid @RequestBody ProductDTO productDTO)
    {
        ProductDTO productResponse=productService.UpdateProductsById(productId, productDTO);
        return new ResponseEntity<>(productResponse,  HttpStatus.OK);
    }
    
    
    @DeleteMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> DeleteProductsById(@PathVariable Long productId
        )
    {
        ProductDTO productResponse=productService.DeleteProductsById(productId);
        return new ResponseEntity<>(productResponse,  HttpStatus.OK);
    }
    @PutMapping("admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> UpdateProductsImage(@PathVariable Long productId, 
        @RequestParam("image") MultipartFile image
        ) throws IOException
    {
        ProductDTO productResponse=productService.UpdateProductImage(productId,image);
        return new ResponseEntity<>(productResponse,  HttpStatus.OK);
    }
}