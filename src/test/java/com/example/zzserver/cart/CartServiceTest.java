package com.example.zzserver.cart;

import com.example.zzserver.cart.dto.CartDto;
import com.example.zzserver.cart.entity.Cart;
import com.example.zzserver.cart.repository.CartRepository;
import com.example.zzserver.cart.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니 담기 성공")
    void addToCart_success() {
        UUID cartId = UUID.randomUUID();

        CartDto.Request req = CartDto.Request.builder()
                .memberId(UUID.randomUUID())
                .roomId(UUID.randomUUID())
                .checkInDate(LocalDateTime.now())
                .checkOutDate(LocalDateTime.now().plusDays(1))
                .roomCount(2L)
                .build();

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> {
                    Cart saved = invocation.getArgument(0);
                    return Cart.builder()
                            .id(cartId)
                            .memberId(saved.getMemberId())
                            .roomId(saved.getRoomId())
                            .checkInDate(saved.getCheckInDate())
                            .checkOutDate(saved.getCheckOutDate())
                            .roomCount(saved.getRoomCount())
                            .createdTime(saved.getCreatedTime())
                            .build();
                });

        UUID result = cartService.addToCart(req);

        assertThat(result).isEqualTo(cartId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("유저별 장바구니 조회 성공")
    void getCartByMember_success() {
        UUID memberId = UUID.randomUUID();

        Cart cart1 = Cart.builder()
                .id(UUID.randomUUID())
                .memberId(memberId)
                .roomId(UUID.randomUUID())
                .checkInDate(LocalDateTime.now())
                .checkOutDate(LocalDateTime.now().plusDays(1))
                .roomCount(1L)
                .createdTime(LocalDateTime.now())
                .build();

        Cart cart2 = Cart.builder()
                .id(UUID.randomUUID())
                .memberId(memberId)
                .roomId(UUID.randomUUID())
                .checkInDate(LocalDateTime.now())
                .checkOutDate(LocalDateTime.now().plusDays(2))
                .roomCount(2L)
                .createdTime(LocalDateTime.now())
                .build();

        when(cartRepository.findByMemberId(memberId))
                .thenReturn(List.of(cart1, cart2));

        List<CartDto.Response> result = cartService.getCartByMember(memberId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMemberId()).isEqualTo(memberId);
        assertThat(result.get(1).getMemberId()).isEqualTo(memberId);
        verify(cartRepository).findByMemberId(memberId);
    }

    @Test
    @DisplayName("장바구니 삭제 성공")
    void removeFromCart_success() {
        UUID cartId = UUID.randomUUID();

        // deleteById는 반환값이 없기 때문에 just verify
        doNothing().when(cartRepository).deleteById(cartId);

        cartService.removeFromCart(cartId);

        verify(cartRepository, times(1)).deleteById(cartId);
    }
}
