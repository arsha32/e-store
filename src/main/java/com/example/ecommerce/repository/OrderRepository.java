package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.model.OrderProduct;

public interface OrderRepository extends JpaRepository<OrderProduct, Long>{

    
}  