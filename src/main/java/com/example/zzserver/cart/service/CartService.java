package com.example.zzserver.cart.service;

import com.example.zzserver.cart.dto.CartDto;
import com.example.zzserver.cart.entity.Cart;
import com.example.zzserver.cart.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    /**
     * 장바구니 담기
     * @param request 장바구니 생성에 필요한 dto
     * @return uuid 장바구니 엔티티의 pk uuid
     **/
    public UUID addToCart(CartDto.Request request) {

        Cart cart = Cart.builder()
                .memberId(request.getMemberId())
                .roomId(request.getRoomId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .roomCount(request.getRoomCount())
                .createdTime(LocalDateTime.now())
                .build();
        return cartRepository.save(cart).getId();
    }

    // 유저별 장바구니 조회
    public List<CartDto.Response> getCartByMember(UUID memberId) {
        return cartRepository.findByMemberId(memberId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 장바구니 아이템 삭제
    public void removeFromCart(UUID cartId) {
        cartRepository.deleteById(cartId);
    }

    private CartDto.Response toDto(Cart cart) {
        return CartDto.Response
                .builder()
                .id(cart.getId())
                .checkInDate(cart.getCheckInDate())
                .checkOutDate(cart.getCheckOutDate())
                .memberId(cart.getMemberId())
                .roomId(cart.getRoomId())
                .roomCount(cart.getRoomCount())
                .createdTime(cart.getCreatedTime())
                .build();
    }
}
