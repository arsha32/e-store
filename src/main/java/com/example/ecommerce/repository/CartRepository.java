package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ecommerce.model.Carts;

public interface CartRepository extends JpaRepository<Carts, Long>{
   
    @Query("SELECT c FROM Carts c WHERE c.customer.email = ?1")
    Carts findCartByEmail(String loggedInEmail);

    @Query("SELECT c FROM Carts c WHERE c.customer.email = ?1 AND c.id = ?2")
    Carts findCartByEmailAndCartId(String emailId, Long cartId);

    @Query("SELECT c FROM Carts c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.id = ?1")
    List<Carts> findCartsByProductId(Long productId);
}  
