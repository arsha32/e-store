package com.example.ecommerce.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.CartItems;
import com.example.ecommerce.model.Carts;
import com.example.ecommerce.model.OrderProduct;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.AddressRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.responseDTO.OrderDTO;
import com.example.ecommerce.responseDTO.OrderItemDTO;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

     @Autowired
    private CartService cartService;
    @Autowired
    private ModelMapper modelMapper;
    
    @Transactional
    public OrderDTO placeOrder(String mail, Long addressId, String paymentType, String pgStatus, String pgId,
            String pgName, String pgResponseMessage) {
        Carts cart = cartRepository.findCartByEmail(mail);
        if (cart == null) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,"No cart selected in the cart");
        }
         Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT,"Address not present need to address"));
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setEmail(mail);
        orderProduct.setOrderDate(LocalDate.now());
        orderProduct.setTotalAmount(cart.getTotalAmount());
        orderProduct.setOrderStatus("Accepted");
        orderProduct.setAddress(address);

        Payment payment = new Payment(paymentType, pgId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(orderProduct);
        payment = paymentRepository.save(payment);
        orderProduct.setPayment(payment);

        OrderProduct savedOrder = orderRepository.save(orderProduct);
        
        List<CartItems> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,"no products present in the cart");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItems cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);

        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();

            // Reduce stock quantity
            product.setQuantity(product.getQuantity() - quantity);

            // Save product back to the database
            productRepository.save(product);

            // Remove items from cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });
        
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
    
}
