package com.example.ecommerce.service;
 
 
import java.io.IOException; 
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus; 
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.ecommerce.model.CartItems;
import com.example.ecommerce.model.Carts;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.responseDTO.CartDTO;
import com.example.ecommerce.responseDTO.ProductDTO;
import com.example.ecommerce.responseDTO.ProductResponse;

import jakarta.transaction.Transactional;
 

@Service
public class ProductService {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private FileService fileService;
    
    @Value("${image.path}")
    private String path;
    @Value("${image.base.url}")
    private String imageBaseUrl;

    public ProductDTO createProduct(Long categoryId, ProductDTO productDTO) {
        Product product=modelMapper.map(productDTO,Product.class);
        Optional<Category> categoryOPT = categoryRepository.findById(categoryId);
        if(!categoryOPT.isPresent())
        {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "With id "+categoryId+ " no category present");
        }
        Category category=categoryOPT.get();
        product.setProductCategory(category);
        double specialPrice=product.getPrice()-((product.getDiscount()*0.01)*product.getPrice());
        product.setSpecialPrice(specialPrice);
        product.setImage(constructImage(productDTO.getImage()));
        Product savedProduct=productRepository.save(product);
        return modelMapper.map(savedProduct,ProductDTO.class);                        
    }

    public ProductResponse getALLProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String category, String keyword) {
        Sort sort=sortOrder.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    :Sort.by(sortBy).descending();
        Pageable pageDetails=PageRequest.of(pageNumber, pageSize, sort);

        Specification<Product> spec=Specification.allOf();

        if(keyword!=null && !keyword.isEmpty())
        {
            spec=spec.and((root,query,criteriaBuilder)->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")),"%"+keyword.toLowerCase()+"%")
            );
        } 
        if(category!=null && !category.isEmpty())
        { 
            spec=spec.and((root,query,criteriaBuilder)->{ 
               return criteriaBuilder.like(root.get("productCategory").get("categoryName"), category 
        );}
            );
        }

        Page<Product> productPage=productRepository.findAll(spec,pageDetails);
        List<Product> product=productPage.getContent();
        
        List<ProductDTO> productDTO=product.stream()
         .map(err->{
           ProductDTO productDto= modelMapper.map(err, ProductDTO.class);
           productDto.setImage(err.getImage());
           return productDto;
        }).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTO);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    public String constructImage(String imageName)
    {
        return imageBaseUrl.endsWith("/")? imageBaseUrl+"imageName": imageBaseUrl+"/"+imageName;
    }
    public ProductResponse getALLProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
         Optional<Category> category= categoryRepository.findById(categoryId);
         if(!category.isPresent())
         {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "With id "+categoryId+ " no category present");
         }
         Sort sort=sortOrder.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    :Sort.by(sortBy).descending();
        Pageable pageDetails=PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage=productRepository.findByProductCategory(category.get(),pageDetails);
        List<Product> products=productPage.getContent();
         List<ProductDTO> productDTO=products.stream()
                            .map(err-> modelMapper.map(err,ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTO);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    public ProductResponse getALLProductsByKeywordy(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort=sortOrder.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    :Sort.by(sortBy).descending();
        Pageable pageDetails=PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage=productRepository.findByProductNameLikeIgnoreCase("%"+keyword+"%", pageDetails);
        List<Product> products=productPage.getContent();
         List<ProductDTO> productDTO=products.stream()
                            .map(err-> modelMapper.map(err,ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTO);
         productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    public ProductDTO UpdateProductsById(Long productId, ProductDTO productDTO) {
        Optional<Product> product= productRepository.findById(productId);
        if(!product.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "With id "+productId+ " no product present");
        }
        Product saveProduct= product.get();
        saveProduct.setDescription(productDTO.getDescription());
        saveProduct.setProductName(productDTO.getProductName());
        saveProduct.setQuantity(productDTO.getQuantity());
        saveProduct.setPrice(productDTO.getPrice());
        saveProduct.setDiscount(productDTO.getDiscount()); 
        double specialPrice=productDTO.getPrice()-((productDTO.getDiscount()*0.01)*productDTO.getPrice());
        saveProduct.setImage(constructImage(productDTO.getImage()));
        saveProduct.setSpecialPrice(specialPrice); 
        Product productTemp = productRepository.save(saveProduct);

        List<Carts> carts = cartRepository.findCartsByProductId(productId);

        // List<CartDTO> cartDTOs = carts.stream().map(cart -> {
        //     CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        //     List<ProductDTO> products = cart.getCartItems().stream()
        //             .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).toList();

        //     cartDTO.setProducts(products);

        //     return cartDTO;

        // }).toList();

        carts.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));


        return modelMapper.map(productTemp,ProductDTO.class);
    }
    
    @Transactional
    public ProductDTO DeleteProductsById(Long productId) {
        Optional<Product> product= productRepository.findById(productId);
        if(!product.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "With id "+productId+ " no product present");
        }
        Product saveProduct= product.get();
        List<Carts> carts = cartRepository.findCartsByProductId(productId); 
        for (Carts cart : carts) {
            CartItems cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
            
            if(cartItem!=null)
            {
                cart.setTotalAmount(cart.getTotalAmount() -
                (cartItem.getProductPrice() * cartItem.getQuantity())); 
                cart.getCartItems().remove(cartItem); 
                cartItemRepository.delete(cartItem);

                // cartRepository.save(cart);
            }

        }  
        
        ProductDTO productDTO=modelMapper.map(saveProduct, ProductDTO.class);
        
        productRepository.delete(saveProduct);  
        return productDTO;
    }

    public ProductDTO UpdateProductImage(Long productId, MultipartFile image) throws IOException {
         Optional<Product> product= productRepository.findById(productId);
        if(!product.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "With id "+productId+ " no product present");
        }
        Product saveProduct= product.get(); 
        String fileName = fileService.UploadImage(path, image);
        saveProduct.setImage(fileName);
        Product productTemp = productRepository.save(saveProduct);
        return modelMapper.map(productTemp, ProductDTO.class);
    }
    
}
