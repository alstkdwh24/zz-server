package com.example.zzserver.payment.service;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.payment.consts.PaymentStatus;
import com.example.zzserver.payment.dto.response.PortOnePaymentDto;
import com.example.zzserver.payment.entity.Payment;
import com.example.zzserver.payment.repository.PaymentRepository;
import com.example.zzserver.reservation.entity.RoomReservations;
import com.example.zzserver.reservation.repository.RoomReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final RoomReservationRepository reservationRepository;

    @Qualifier("portOneTemplate")
    private final RestTemplate restTemplate;

    private final PortOneAuthService portOneAuthService;

    private final PortOnePaymentService portOnePaymentService;


    public Payment ready(UUID reservationId, Long amount) {
        log.info("ready() 시작: reservationId={}, amount={}", reservationId, amount);
        String merchantUid = "order_" + UUID.randomUUID().toString().replace("-", "");
        log.info("merchantUID={}"+merchantUid);
        try {
            RoomReservations reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
            log.info("예약 조회 성공: {}", reservation.getId());

            if (reservation.getStatus() != null && reservation.getStatus().name().equals("CONFIRMED")) {
                throw new CustomException(ErrorCode.RESERVATION_CONFIRM);
            }

            Payment payment = Payment.builder()
                    .reservationId(reservationId)
                    .merchantUid(merchantUid)
                    .amount(amount)
                    .status(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            Payment saved = paymentRepository.save(payment);
            log.info("결제 정보 저장 완료: {}", saved.getMerchantUid());
            return saved;
        } catch (Exception e) {
            log.error("ready() 단계 오류 발생", e);
            throw e;
        }
    }

    //결제 인증 확인
    public void verifySuccess(String portonePaymentId, String merchantUid, String method, Long amount, LocalDateTime approvedAt) {
        log.info("verifySuccess 시작 portonePaymentId={}, merchantUid={}, method={}, amount={}",
                portonePaymentId, merchantUid, method, amount);

        PortOnePaymentDto resp = portOnePaymentService.getPayment(portonePaymentId);
        log.info("PortOne 응답={}", resp);
        String merchantId = resp.getResponse().getMerchant_uid();
        log.info(merchantId);
        if (resp.getResponse() == null) {
            throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        PortOnePaymentDto.PaymentResponse payInfo = resp.getResponse();
        log.info("PortOne 내부응답={}", payInfo);

        Payment payment = paymentRepository.findByMerchantUidForUpdate(merchantUid)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        log.info("조회 내역::"+payment);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATE);
        }

        if (!payment.getAmount().equals(payInfo.getAmount())) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        payment.markSuccess(payInfo.getImp_uid(),payInfo.getPay_method(), approvedAt);

        RoomReservations reservation = reservationRepository.findById(payment.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.confirm();

        log.info("결제 성공 DB 반영 완료");
    }

    public void verifyFail(String portonePaymentId, String pgCode, String pgMessage) {

        Payment payment = paymentRepository.findByPortonePaymentIdForUpdate(portonePaymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATE);
        }

        payment.markFail(pgCode, pgMessage);

        RoomReservations reservation = reservationRepository.findById(payment.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.cancel();
    }

    public void cancel(String portonePaymentId, String pgCode, String pgMessage) {
        Payment payment = paymentRepository.findByPortonePaymentIdForUpdate(portonePaymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_CANCELED);
        }

        // PortOne API에 환불 요청
        portOnePaymentService.cancelPayment(portonePaymentId, pgMessage);

        payment.markCanceled(pgCode, pgMessage);

        RoomReservations reservation = reservationRepository.findById(payment.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.cancel();
    }

    public void refund(String portonePaymentId, String reason) {
        Payment payment = paymentRepository.findByPortonePaymentIdForUpdate(portonePaymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATE);
        }

        // PortOne API에 환불 요청
        portOnePaymentService.cancelPayment(portonePaymentId, reason);

        payment.markCanceled("USER_CANCEL", reason);

        RoomReservations reservation = reservationRepository.findById(payment.getReservationId())
                    .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.cancel();
    }

    /**
     * PortOne V1 API에서 결제 단건 조회
     */
    public String getPaymentFromPortone(String portonePaymentId) {
        String url = "https://api.iamport.kr/payments/" + portonePaymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(portOneAuthService.getAccessToken()); // 토큰 사용

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
