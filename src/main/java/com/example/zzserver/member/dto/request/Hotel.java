package com.example.zzserver.member.dto.request;

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

    // 생성자
    public Hotel(String name, String location, String category, Double rating, String price, String imageUrl,
            Integer reviewCount, Boolean hasCoupon, String originalPrice) {
        this.name = name;
        this.location = location;
        this.category = category;
        this.rating = rating;
        this.price = price;
        this.imageUrl = imageUrl;
        this.reviewCount = reviewCount;
        this.hasCoupon = hasCoupon;
        this.originalPrice = originalPrice;
    }

    // Getter methods (Thymeleaf에서 필요)
    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public Double getRating() {
        return rating;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public Boolean getHasCoupon() {
        return hasCoupon;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    // Setter methods (선택사항)
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setHasCoupon(Boolean hasCoupon) {
        this.hasCoupon = hasCoupon;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    // getter methods...
}
