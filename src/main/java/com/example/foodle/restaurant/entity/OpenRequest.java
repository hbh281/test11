package com.example.foodle.restaurant.entity;

import com.example.foodle.BaseEntity;
import com.example.foodle.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OpenRequest extends BaseEntity {
    @ManyToOne()
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    @Setter
    private Boolean isApproved;
    @Setter
    private String reason;
}