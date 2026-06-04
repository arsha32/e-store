package com.example.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.model.Customer;
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long>{

    Optional<Customer> findByCustomerName(String username);

    boolean existsByCustomerName(String username);

    boolean existsByEmail(String email);
    
}
