package com.example.foodle.restaurant;

import com.example.foodle.auth.repo.UserRepo;
import com.example.foodle.restaurant.dto.OpenRequestDto;
import com.example.foodle.restaurant.dto.RestaurantDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private UserRepo userRepository;
    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("/request-open")
    public ResponseEntity<OpenRequestDto> createRestaurantAndSendRequest(@RequestBody RestaurantDto restaurantDto) {
        try {
            OpenRequestDto openRequestDto = restaurantService.createRestaurantAndSendRequest(restaurantDto);
            return ResponseEntity.ok(openRequestDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @GetMapping
    public ResponseEntity<RestaurantDto> getRestaurant() {
        RestaurantDto restaurant = restaurantService.getRestaurantByOwner();
        return ResponseEntity.ok(restaurant);
    }
    @PutMapping("/update")
    public ResponseEntity<RestaurantDto> updateRestaurant( @RequestBody RestaurantDto restaurantDto) {
        RestaurantDto updatedRestaurant = restaurantService.updateRestaurant( restaurantDto);
        return new ResponseEntity<>(updatedRestaurant, HttpStatus.OK);
    }
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable Long restaurantId) {
        RestaurantDto restaurant = restaurantService.getRestaurantById(restaurantId);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<RestaurantDto>> getAllRestaurants(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RestaurantDto> restaurants = restaurantService.getAllRestaurants(pageable);
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    @PostMapping("/upload-profile-image")
    public ResponseEntity<String> uploadProfileImage(
                                                     @RequestParam("file") MultipartFile file) {
        try {
            RestaurantDto fileName = restaurantService.uploadProfileImage(file);
            return new ResponseEntity<>("Image uploaded successfully: " + fileName, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not upload the image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


