package com.example.foodle.auth.entity;


import com.example.foodle.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {
    @Column(unique = true)
    private String username;
    private String password;
    private String name;
    private String nickname;
    private Integer age;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String phone;
    private String profileImg;
    @Builder.Default
    private String roles = "ROLE_INACTIVE";
}
