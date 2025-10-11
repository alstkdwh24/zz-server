package com.example.zzserver.cart.repository;

import com.example.zzserver.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    List<Cart> findByMemberId(UUID memberId);
}
