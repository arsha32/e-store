package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
 
import com.example.ecommerce.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

    
}  