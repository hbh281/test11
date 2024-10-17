package com.example.foodle.restaurant.dto;

import com.example.foodle.restaurant.entity.OpenRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OpenRequestDto {
    private Long id;
    private Long resId;
    private String resName;
    private Boolean isApproved;
    private String reason;

    public static OpenRequestDto fromEntity(OpenRequest entity) {
        log.info("loi open dto");
        return OpenRequestDto.builder()
                .id(entity.getId())
                .resId(entity.getRestaurant().getId())
                .resName(entity.getRestaurant().getName())
                .isApproved(entity.getIsApproved())
                .reason(entity.getReason())
                .build();
    }
}
