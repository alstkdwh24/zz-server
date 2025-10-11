package com.example.zzserver.member.dto.request;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {
    private String name;
    private String location;
    private String category;
    private Double rating;
    private String price;
    private String imageUrl;
    private Integer reviewCount;
    private Boolean hasCoupon;
    private String originalPrice;

}
