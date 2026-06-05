package com.example.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.ecommerce.config.AuthUtil;
import com.example.ecommerce.model.CartItems;
// import com.example.ecommerce.util.AuthUtil;
import com.example.ecommerce.model.Carts;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.responseDTO.CartDTO;
import com.example.ecommerce.responseDTO.CartItemDTO;
import com.example.ecommerce.responseDTO.ProductDTO;

import jakarta.transaction.Transactional;

@Service
public class CartService {
    @Autowired
    private AuthUtil authUtil;
    
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
     ModelMapper modelMapper;

    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Carts cart  = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "With id "+productId+ " no product present" ));

        CartItems cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cartitem present" );
        }

        if (product.getQuantity() == 0) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "product is out of stock" );
        }

        if (product.getQuantity() < quantity) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "That many quantity of product not present " );
        }

        CartItems newCartItem = new CartItems();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalAmount(cart.getTotalAmount() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItems> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }
    
    private Carts createCart() {
        Carts userCart  = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }
        Carts cart = new Carts();
        cart.setTotalAmount(0.00);
        cart.setCustomer(authUtil.loggedInUser());
        Carts newCart =  cartRepository.save(cart);

        return newCart;
    }

    public List<CartDTO> getAllCarts() {
        List<Carts> carts=cartRepository.findAll();
        if(carts.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No carts presents" );
        }

        List<CartDTO> cartDTO = carts.stream().map(cart->{
                                CartDTO cartDto = modelMapper.map(cart, CartDTO.class);
                                
                                List<ProductDTO> products=cart.getCartItems().stream().map(e->{
                                            ProductDTO product=modelMapper.map(e.getProduct(),ProductDTO.class);
                                            product.setQuantity(e.getQuantity());
                                            return product;
                                        }).toList();

                                cartDto.setProducts(products);
                                return cartDto;
                            }).toList();
        return cartDTO;
    }

    public CartDTO getCart(String emailId, Long cartId) {
        Carts cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "With this mail no carts present" );
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        
        List<ProductDTO> products=cart.getCartItems().stream().map(e->{
                                            ProductDTO product=modelMapper.map(e.getProduct(),ProductDTO.class);
                                            product.setImage(e.getProduct().getImage());
                                            product.setQuantity(e.getQuantity());
                                            return product;
                                        }).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Carts userCart = cartRepository.findCartByEmail(emailId);
        Long cartId  = userCart.getCartId();

        Carts cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "With this mail no carts present"));

        Product product = productRepository.findById(productId)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "With this id no product ispresent"));
        if (product.getQuantity() == 0) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "This product "+product.getProductName()+" is not present");
        }

        if (product.getQuantity() < quantity) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "This product "+product.getProductName()+" of quantity is not available present");
        }

        CartItems cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "this product is not present in the cart");
        }

        // Calculate new quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        // // Validation to prevent negative quantities
        // if (newQuantity < 0) {
        //     throw new ResponseStatusException(HttpStatus.NO_CONTENT, "This product "+product.getProductName()+" of quantity is not available present");
        // }

       
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalAmount(cart.getTotalAmount() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);

        CartItems updatedItem = cartItemRepository.save(cartItem);
         if(updatedItem.getQuantity() == 0){
            cart.getCartItems().remove(updatedItem);
           deleteProductFromCart(cartId, productId);
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        
        List<ProductDTO> products=cart.getCartItems().stream().map(e->{
                                            ProductDTO pro=modelMapper.map(e.getProduct(),ProductDTO.class);
                                            pro.setQuantity(e.getQuantity());
                                            return pro;
                                        }).toList();
        cartDTO.setProducts(products);

        return cartDTO;
    }

    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId) {
        Carts cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "With this mail no carts present"));

        CartItems cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "this cart don't have any products");
        }

        cart.setTotalAmount(cart.getTotalAmount() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    } 
    
    public void updateProductInCarts(Long cartId, Long productId) {
        Carts cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "this cart of id is not present" ));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "this product of id is not present" ));

        CartItems cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no cartitem is present" );
        }

        double cartPrice = cart.getTotalAmount()
                - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setDiscount(product.getDiscount());
        cart.setTotalAmount(cartPrice
                + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);
    }

    @Transactional
    public String createOrUpdateCart(List<CartItemDTO> cartItemsDTO) {
        
        String emailId = authUtil.loggedInEmail();
        Carts userCart = cartRepository.findCartByEmail(emailId);
        
        if(userCart ==null)
        {
            userCart=new Carts();
            userCart.setTotalAmount(0.00);
            userCart.setCustomer(authUtil.loggedInUser());
            userCart =  cartRepository.save(userCart);
        }
        else{
            //delete existing data
             cartItemRepository.deleteCartItemByCartId(userCart.getCartId());
        }
        double total=0.0;
        for(CartItemDTO cartItemDTO: cartItemsDTO)
        {
            long productId=cartItemDTO.getProductId();
            int quantity= cartItemDTO.getQuantity();
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "this product of id is not present" ));
            total+=product.getSpecialPrice()*quantity;
            CartItems cartItems= new CartItems();
            cartItems.setCart(userCart);
            cartItems.setProduct(product);
            cartItems.setQuantity(quantity);
            cartItems.setDiscount(product.getDiscount());
            cartItems.setProductPrice(product.getSpecialPrice());
            cartItemRepository.save(cartItems);
        }
        userCart.setTotalAmount(total);
        
        cartRepository.save(userCart);
        return "cart created successfully";
    }

}
