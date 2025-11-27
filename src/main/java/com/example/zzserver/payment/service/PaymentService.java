package com.example.zzserver.payment.service;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.payment.consts.PaymentStatus;
import com.example.zzserver.payment.dto.response.PortOnePaymentDto;
import com.example.zzserver.payment.entity.Payment;
import com.example.zzserver.payment.repository.PaymentRepository;
import com.example.zzserver.reservation.consts.ReservationStatus;
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
import java.util.Optional;
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
        RoomReservations reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        log.info("예약 조회 성공: {}", reservation.getId());

        if (reservation.getStatus() != null && reservation.getStatus().name().equals("CONFIRMED")) {
            throw new CustomException(ErrorCode.RESERVATION_CONFIRM);
        }

        Optional<Payment> existing  = paymentRepository.findByReservationIdForUpdate(reservationId);
        if(existing.isPresent()) {
            log.info("기존 PENDING 결제 반환: {}", existing.get().getMerchantUid());
            return existing.get();
        }

        String merchantUid = "order_" + UUID.randomUUID().toString().replace("-", "");
        log.info("merchantUID={}"+merchantUid);

        Payment payment = savePayment(reservationId,merchantUid,amount);
        log.info("결제 정보 저장 완료: {}", payment.getMerchantUid());
        return payment;
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

        if (!merchantUid.equals(payInfo.getMerchant_uid())) {
            throw new CustomException(ErrorCode.PAYMENT_MERCHANT_MISMATCH);
        }

        Payment payment = paymentRepository.findByMerchantUidForUpdate(merchantUid)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        log.info("조회 내역::"+payment);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATE);
        }

        if (!payment.getAmount().equals(payInfo.getAmount())) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }
        // 예약 조회(락)
        RoomReservations reservation = reservationRepository.findByIdForUpdate(payment.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            log.info("이미 CONFIRMED - 멱등 처리");
            return;
        }
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("이미 SUCCESS - 멱등 처리");
            return;
        }
        // 결제 승인 처리
        payment.markSuccess(payInfo.getImp_uid(), payInfo.getPay_method(), approvedAt);
        // 예약 확인
        reservation.confirm();

        log.info("결제 성공 DB 반영 완료");
    }

    public void verifyFail(String portonePaymentId, String pgCode, String pgMessage) {
        // 결제 조회
        Payment payment = paymentRepository.findByPortonePaymentIdForUpdate(portonePaymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        // 이미 실패 처리된 경우 멱등성
        if (payment.getStatus() == PaymentStatus.FAILED) {
            log.info("이미 실패 처리됨 - 멱등 처리");
            return;
        }
        
        // 성공 처리한 경우
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATE);
        }
        // 실패 처리 변경
        payment.markFail(pgCode, pgMessage);

        RoomReservations reservation = reservationRepository.findByIdForUpdate(payment.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.cancel();
    }

    public void cancel(String portonePaymentId, String pgCode, String pgMessage) {
        Payment payment = paymentRepository.findByPortonePaymentIdForUpdate(portonePaymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            return;
        }
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATE);
        }

        // PortOne API에 환불 요청
        portOnePaymentService.cancelPayment(portonePaymentId, pgMessage);

        payment.markCanceled(pgCode, pgMessage);

        RoomReservations reservation = reservationRepository.findByIdForUpdate(payment.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.cancel();
    }

    public void refund(String portonePaymentId, String reason) {
        Payment payment = paymentRepository.findByPortonePaymentIdForUpdate(portonePaymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            log.info("이미 CANCELED - 멱등 처리");
            return;
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATE);
        }

        // PortOne API에 환불 요청
        portOnePaymentService.cancelPayment(portonePaymentId, reason);

        payment.markCanceled("USER_CANCEL", reason);

        RoomReservations reservation = reservationRepository.findByIdForUpdate(payment.getReservationId())
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


    private Payment savePayment(UUID reservationId,String merchantUid,Long amount) {
        Payment payment= Payment.builder()
                .reservationId(reservationId)
                .merchantUid(merchantUid)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        return paymentRepository.save(payment);
    }
}
