package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.ecommerce.model.CartItems;

public interface CartItemRepository extends JpaRepository<CartItems, Long>{
    @Query("SELECT ci FROM CartItems ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    CartItems findCartItemByProductIdAndCartId(Long cartId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItems ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItems ci WHERE ci.cart.id = ?1")
    void deleteCartItemByCartId(Long cartId);
}  