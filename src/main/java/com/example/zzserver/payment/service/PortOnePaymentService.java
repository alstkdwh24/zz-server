package com.example.zzserver.payment.service;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.payment.consts.ExternalApiConstants;
import com.example.zzserver.payment.dto.response.PortOnePaymentDto;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class PortOnePaymentService {

    private final RestTemplate restTemplate;
    private final PortOneAuthService portOneAuthService;

    public PortOnePaymentDto getPayment(String impUid) {

        String url = ExternalApiConstants.GET_PAYMENT.format(impUid);
        String token = portOneAuthService.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PortOnePaymentDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, PortOnePaymentDto.class);
        return response.getBody();
    }

    public void cancelPayment(String impUid, String reason) {

        String url = ExternalApiConstants.CANCEL_URL.getUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(portOneAuthService.getAccessToken());

        Map<String, Object> body = new HashMap<>();

        body.put("imp_uid", impUid);
        body.put("reason", reason);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);
        }
    }
}
