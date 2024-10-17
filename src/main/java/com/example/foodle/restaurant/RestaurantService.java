package com.example.foodle.restaurant;


import com.example.foodle.FileHandlerUtils;
import com.example.foodle.auth.AuthenticationFacade;
import com.example.foodle.auth.dto.UserDto;
import com.example.foodle.auth.entity.UserEntity;
import com.example.foodle.auth.repo.UserRepo;
import com.example.foodle.restaurant.dto.OpenRequestDto;
import com.example.foodle.restaurant.dto.RestaurantDto;
import com.example.foodle.restaurant.entity.OpenRequest;
import com.example.foodle.restaurant.entity.Restaurant;
import com.example.foodle.restaurant.repository.ResOpenReqRepo;
import com.example.foodle.restaurant.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;
    private ResOpenReqRepo resOpenReqRepo;
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private final AuthenticationFacade facade;
    @Autowired
    private final FileHandlerUtils fileHandlerUtils;



    public OpenRequestDto createRestaurantAndSendRequest(RestaurantDto dto) {
        // Lấy thông tin user hiện tại (là chủ sở hữu nhà hàng)
        UserEntity owner = facade.extractUser();

        // Kiểm tra nếu user không có quyền tạo nhà hàng
        if (!owner.getRoles().equals("ROLE_OWNER")) {
            throw new RuntimeException("User không có quyền tạo nhà hàng");
        }

        // Tạo mới nhà hàng
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setAddress(dto.getAddress());
        restaurant.setCapacity(dto.getCapacity());
        restaurant.setPhoneNumber(dto.getPhoneNumber());
        restaurant.setDescription(dto.getDescription());
        restaurant.setRegistrationNum(dto.getRegistrationNum());
        restaurant.setCategory(dto.getCategory());
        restaurant.setOwner(owner);

        restaurant.setStatus(Restaurant.Status.PREPARING);  // Đặt trạng thái là PREPARING (đang chờ phê duyệt)

        // Lưu nhà hàng vào cơ sở dữ liệu
        restaurantRepository.save(restaurant);
        log.info("loi");
        log.info(restaurant.toString());
        // Tạo yêu cầu mở nhà hàng
        OpenRequest openRequest = new OpenRequest();
        openRequest.setReason("open request");
        openRequest.setIsApproved(false);

        log.info("loi2");
        // Lưu yêu cầu vào cơ sở dữ liệu
        resOpenReqRepo.save(openRequest);
        log.info("loi3");
        // Trả về DTO của yêu cầu đã được tạo
        OpenRequestDto dto1 = OpenRequestDto.fromEntity(openRequest);
        log.info(dto1.toString());

        return dto1;
    }


    public RestaurantDto getRestaurantByOwner() {
        // Lấy thông tin người dùng hiện tại từ AuthenticationFacade
        UserEntity owner = facade.extractUser();

        if (owner == null) {
            throw new RuntimeException("User không tồn tại");
        }

        Restaurant restaurant = restaurantRepository.findByOwner(owner)
                .orElseThrow(() -> new RuntimeException("Nhà hàng không tồn tại"));

        return RestaurantDto.fromEntity(restaurant);
    }


    public RestaurantDto updateRestaurant(RestaurantDto restaurantDto) {
        UserEntity owner = facade.extractUser();
        Restaurant restaurant = restaurantRepository.findByOwner(owner).orElseThrow(() -> new RuntimeException("nhà hàng không tồn tại")) ;
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new RuntimeException("Nhà hàng không tồn tại"));

        restaurant.setName(restaurantDto.getName());
        restaurant.setAddress(restaurantDto.getAddress());
        restaurant.setCapacity(restaurantDto.getCapacity());
        restaurant.setPhoneNumber(restaurantDto.getPhoneNumber());
        restaurant.setDescription(restaurantDto.getDescription());
        restaurant.setRegistrationNum(restaurantDto.getRegistrationNum());
        restaurant.setCategory(restaurantDto.getCategory());
        restaurant.setStatus(restaurantDto.getStatus());
        restaurant.setCloseReason(restaurantDto.getCloseReason());

        return RestaurantDto.fromEntity(restaurantRepository.save(restaurant));
    }

    public RestaurantDto getRestaurantById(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Nhà hàng không tồn tại"));
        return RestaurantDto.fromEntity(restaurant);
    }
    public Page<RestaurantDto> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable).map(RestaurantDto::fromEntity);
    }

    public RestaurantDto uploadProfileImage(MultipartFile file) throws IOException {
        UserEntity owner = facade.extractUser();
        Restaurant restaurant = restaurantRepository.findByOwner(owner).orElseThrow(() -> new RuntimeException("nhà hàng không tồn tại")) ;


        String requestPath = fileHandlerUtils.saveFile(
                String.format("users/%d/", owner.getId()),
                "profile",
                file
        );

        restaurant.setProfileImage(requestPath);
        restaurantRepository.save(restaurant);
        return RestaurantDto.fromEntity(restaurant);
    }
}

