package com.example.zzserver.payment.restcontroller;

import com.example.zzserver.payment.dto.request.PaymentRequestDto;
import com.example.zzserver.payment.dto.response.PaymentResponseDto;
import com.example.zzserver.payment.entity.Payment;
import com.example.zzserver.payment.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 준비
    @PostMapping("/ready")
    public ResponseEntity<PaymentResponseDto> ready(@RequestBody PaymentRequestDto.ReadyRequest request) {
        Payment payment = paymentService.ready(request.getReservationId(),request.getAmount());
        return ResponseEntity.ok(PaymentResponseDto.from(payment));
    }

    // 결제 성공 처리
    @PostMapping("/success")
    public ResponseEntity<Void> success(@RequestBody PaymentRequestDto.SuccessRequest request) {
        paymentService.verifySuccess(request.getPortonePaymentId(),request.getMerchantUid(), request.getMethod(),request.getAmount(), request.getApprovedAt());
        return ResponseEntity.ok().build();
    }

    // 결제 실패 처리
    @PostMapping("/fail")
    public ResponseEntity<Void> fail(@RequestBody PaymentRequestDto.FailRequest request) {
        paymentService.verifyFail(request.getPortonePaymentId(), request.getPgCode(), request.getPgMessage());
        return ResponseEntity.ok().build();
    }

    // 결제 취소
    @PostMapping("/cancel")
    public ResponseEntity<Void> cancel(@RequestBody PaymentRequestDto.CancelRequest request) {
        paymentService.cancel(request.getPortonePaymentId(), request.getPgCode(), request.getPgMessage());
        return ResponseEntity.ok().build();
    }

    // 환불
    @PostMapping("/refund")
    public ResponseEntity<Void> refund(@RequestParam String portonePaymentId,
                                       @RequestParam String reason) {
        paymentService.refund(portonePaymentId, reason);
        return ResponseEntity.ok().build();
    }

    // 결제 단건 조회 (PortOne API 호출)
    @GetMapping("/{portonePaymentId}")
    public ResponseEntity<String> getPayment(@PathVariable String portonePaymentId) {
        String result = paymentService.getPaymentFromPortone(portonePaymentId);
        System.out.println(result);
        return ResponseEntity.ok(result);
    }
}
