package com.example.zzserver.payment.service;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.payment.consts.ExternalApiConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortOneAuthService {

    @Qualifier("portOneTemplate")
    private final RestTemplate restTemplate;

    @Value("${portone.v1.api-key}")
    private String apiKey;

    @Value("${portone.v1.api-secret}")
    private String apiSecret;

    private String cachedToken;
    private LocalDateTime expiry;

    public String getAccessToken() {
        if (cachedToken != null && expiry != null && expiry.isAfter(LocalDateTime.now())) {
            return cachedToken;
        }

        String url = ExternalApiConstants.ACCESS_TOKEN.getUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "imp_key", apiKey,
                "imp_secret", apiSecret
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);
        }

        Map<String, Object> resp = (Map<String, Object>) response.getBody().get("response");
        cachedToken = (String) resp.get("access_token");
        int expireSec = (Integer) resp.get("expired_at");
        expiry = LocalDateTime.now().plusSeconds(expireSec - 60);

        return cachedToken;
    }
}
