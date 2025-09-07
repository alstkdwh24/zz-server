package com.example.zzserver.cart.controller;

import com.example.zzserver.cart.dto.CartDto;
import com.example.zzserver.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/")
    public ResponseEntity<UUID> addToCart(@RequestBody @Valid CartDto.Request request) {
        UUID cartId = cartService.addToCart(request);
        return ResponseEntity.ok(cartId);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<CartDto.Response>> getCartByMember(@PathVariable UUID memberId) {
        return ResponseEntity.ok(cartService.getCartByMember(memberId));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable UUID cartId) {
        cartService.removeFromCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
