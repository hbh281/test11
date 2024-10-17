package com.example.foodle.restaurant.dto;

import com.example.foodle.restaurant.entity.Restaurant;
import lombok.*;

import java.time.LocalTime;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phoneNumber;
    private String registrationNum;
    private Integer capacity;
    private String profileImage;
    private Restaurant.Category category;
    private Restaurant.Status status;
    private String closeReason;
    private LocalTime openTime;
    private LocalTime closeTime;

    public static RestaurantDto fromEntity(Restaurant entity) {
        return fromEntity(entity, false);
    }

    public static RestaurantDto fromEntity(Restaurant entity, boolean fullInfo) {
        RestaurantDtoBuilder builder = RestaurantDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .registrationNum(entity.getRegistrationNum())
                .address(entity.getAddress())
                .description(entity.getDescription())
                .capacity(entity.getCapacity())
                .category(entity.getCategory())
                .profileImage(entity.getProfileImage())
                .openTime(entity.getOpenTime())
                .closeTime(entity.getCloseTime())
                .status(entity.getStatus());

        if (fullInfo) builder
                .closeReason(entity.getCloseReason());
        return builder.build();
    }
}