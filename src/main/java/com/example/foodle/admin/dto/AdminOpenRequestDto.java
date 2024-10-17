package com.example.foodle.admin.dto;


import com.example.foodle.restaurant.dto.RestaurantDto;
import com.example.foodle.restaurant.entity.OpenRequest;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminOpenRequestDto {
    private Long id;
    private RestaurantDto restaurant;
    private Boolean isApproved;
    private String reason;

    public static AdminOpenRequestDto fromEntity(OpenRequest entity) {
        return AdminOpenRequestDto.builder()
                .id(entity.getId())
                .restaurant(RestaurantDto.fromEntity(entity.getRestaurant()))
                .isApproved(entity.getIsApproved())
                .reason(entity.getReason())
                .build();
    }
}
