package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{
    
}
