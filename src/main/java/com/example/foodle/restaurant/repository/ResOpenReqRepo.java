package com.example.foodle.restaurant.repository;

import com.example.foodle.restaurant.entity.OpenRequest;
import com.example.foodle.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResOpenReqRepo extends JpaRepository<OpenRequest, Long> {
    List<OpenRequest> findByIsApprovedIsNull();
}
