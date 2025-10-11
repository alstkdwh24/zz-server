package com.example.zzserver.payment;


import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.payment.consts.PaymentStatus;
import com.example.zzserver.payment.dto.response.PortOnePaymentDto;
import com.example.zzserver.payment.entity.Payment;
import com.example.zzserver.payment.repository.PaymentRepository;
import com.example.zzserver.payment.service.PaymentService;
import com.example.zzserver.payment.service.PortOneAuthService;
import com.example.zzserver.payment.service.PortOnePaymentService;
import com.example.zzserver.reservation.consts.ReservationStatus;
import com.example.zzserver.reservation.entity.RoomReservations;
import com.example.zzserver.reservation.repository.RoomReservationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RoomReservationRepository reservationRepository;

    @Mock
    private PortOneAuthService portOneAuthService;

    @Mock
    private PortOnePaymentService portOnePaymentService;

    @InjectMocks
    private PaymentService paymentService;

    private UUID reservationId;
    private RoomReservations reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reservationId = UUID.randomUUID();
        reservation = RoomReservations.builder()
                .id(reservationId)
                .status(ReservationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("ready(): 예약정보와 금액을 기반으로 결제정보 생성")
    void ready_success() {
        // given
        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        given(paymentRepository.save(any(Payment.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Payment result = paymentService.ready(reservationId, 1000L);

        // then
        assertThat(result.getAmount()).isEqualTo(1000L);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("verifySuccess(): 결제성공 후 Payment 상태 갱신")
    void verifySuccess_success() {
        // given
        String merchantUid = "order_" + UUID.randomUUID();
        String impUid = "imp_123456789";
        LocalDateTime now = LocalDateTime.now();

        PortOnePaymentDto.PaymentResponse mockResponse = new PortOnePaymentDto
                .PaymentResponse()
                .builder()
                .imp_uid(impUid)
                .merchant_uid(merchantUid)
                .amount(1000L)
                .pay_method("card")
                .status("paid")
                .build();

        PortOnePaymentDto mockDto = new PortOnePaymentDto(0, null, mockResponse);

        Payment payment = Payment.builder()
                .merchantUid(merchantUid)
                .amount(1000L)
                .status(PaymentStatus.PENDING)
                .reservationId(reservationId)
                .build();

        given(portOnePaymentService.getPayment(impUid)).willReturn(mockDto);
        given(paymentRepository.findByMerchantUidForUpdate(merchantUid))
                .willReturn(Optional.of(payment));
        given(reservationRepository.findById(payment.getReservationId()))
                .willReturn(Optional.of(reservation));

        // when
        paymentService.verifySuccess(impUid, merchantUid, "card", 1000L, now);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getMethod()).isEqualTo("card");
    }

    @Test
    @DisplayName("cancel(): 결제 취소 요청 시 상태 변경 및 PortOne API 호출")
    void cancel_success() {
        Payment payment = Payment.builder()
                .portonePaymentId("imp_9999")
                .status(PaymentStatus.SUCCESS)
                .reservationId(reservationId)
                .build();

        given(paymentRepository.findByPortonePaymentIdForUpdate("imp_9999"))
                .willReturn(Optional.of(payment));
        given(reservationRepository.findById(reservationId))
                .willReturn(Optional.of(reservation));

        paymentService.cancel("imp_9999", "C001", "사용자 요청 취소");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        verify(portOnePaymentService) .cancelPayment("imp_9999", "사용자 요청 취소");
    }

    @Test
    @DisplayName("refund(): 결제 환불 요청 시 상태 변경 및 PortOne API 호출")
    void refund_success() {
        Payment payment = Payment.builder()
                .portonePaymentId("imp_8888")
                .status(PaymentStatus.SUCCESS)
                .reservationId(reservationId)
                .build();

        given(paymentRepository.findByPortonePaymentIdForUpdate("imp_8888"))
                .willReturn(Optional.of(payment));
        given(reservationRepository.findById(reservationId))
                .willReturn(Optional.of(reservation));

        paymentService.refund("imp_8888", "테스트 환불");

        Assertions.assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        Mockito.verify(portOnePaymentService).cancelPayment("imp_8888", "테스트 환불");
    }

    @Test
    @DisplayName("ready(): 예약정보가 없으면 예외 발생")
    void ready_fail_reservationNotFound() {
        given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());
        assertThatThrownBy(() -> paymentService.ready(reservationId, 1000L))
                .isInstanceOf(CustomException.class);
    }
}
