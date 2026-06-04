package com.example.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.config.AuthUtil;
import com.example.ecommerce.responseDTO.OrderDTO;
import com.example.ecommerce.responseDTO.OrderRequestDTO;
import com.example.ecommerce.service.OrderService;

@RestController
@RequestMapping("api")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/order/users/payments/{paymentType}")
    public ResponseEntity<OrderDTO> placeOrder(@PathVariable String paymentType, @RequestBody OrderRequestDTO orderRequestDTO)
    {
        String mail= authUtil.loggedInEmail();
        OrderDTO orderDTO = orderService.placeOrder(mail, 
                                         orderRequestDTO.getAddressId(),
                                         paymentType,
                                        orderRequestDTO.getPgStatus(),
                                        orderRequestDTO.getPgId(), 
                                        orderRequestDTO.getPgName(),
                                        orderRequestDTO.getPgResponseMessage());
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
