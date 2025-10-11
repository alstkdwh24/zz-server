package com.example.zzserver.config.dto;

import com.example.zzserver.config.message.Messages;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class TokenResponseDTO {
    private UUID id;
    @JsonProperty("access_token")
    private String access_token;
    @JsonProperty("refresh_token")
    private String refresh_token;

    private Messages message;

    public void changeMessage(Messages message) {
        if ((message != null)) {
            this.message = message;
        }
    }






}
