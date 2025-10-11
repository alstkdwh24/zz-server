package com.example.zzserver.payment;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.payment.service.PortOneAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PortOneAuthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PortOneAuthService authService;

    @BeforeEach
    void init() {
        // @Value 로 주입될 필드를 수동 세팅 (스프링 컨텍스트 안 띄우므로)
        ReflectionTestUtils.setField(authService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(authService, "apiSecret", "test-api-secret");
    }

    @Test
    @DisplayName("getAccessToken(): 토큰이 정상적으로 생성되고 캐싱된다")
    void getAccessToken_success() {
        // given
        Map<String, Object> fakeResponse = Map.of(
                "access_token", "mock_access_token",
                "expired_at", 300
        );
        Map<String, Object> body = Map.of("response", fakeResponse);
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        given(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .willReturn(responseEntity);

        // when
        String token = authService.getAccessToken();

        // then
        assertThat(token).isEqualTo("mock_access_token");
    }

    @Test
    @DisplayName("getAccessToken(): 캐시가 초기화되면 새 토큰을 발급한다")
    void getAccessToken_refresh() {
        // given
        ResponseEntity<Map> badResponse = new ResponseEntity<>(Map.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        given(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .willReturn(badResponse);

        // when & then
        assertThatThrownBy(() -> authService.getAccessToken())
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("결제 취소(환불)에 실패했습니다.");
    }

    @Test
    @DisplayName("getAccessToken(): 내부 오류 발생 시 예외 처리")
    void getAccessToken_fail() {
        // given
        Map<String, Object> fakeResponse = Map.of(
                "access_token", "cached_token",
                "expired_at", 300
        );
        Map<String, Object> body = Map.of("response", fakeResponse);
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        given(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .willReturn(responseEntity);

        // when
        String token1 = authService.getAccessToken();
        String token2 = authService.getAccessToken();

        // then
        assertThat(token1).isEqualTo(token2);
    }
}
