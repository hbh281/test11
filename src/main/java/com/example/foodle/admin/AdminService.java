package com.example.foodle.admin;

import com.example.foodle.admin.dto.AdminOpenRequestDto;
import com.example.foodle.restaurant.entity.OpenRequest;
import com.example.foodle.restaurant.entity.Restaurant;
import com.example.foodle.restaurant.repository.ResOpenReqRepo;
import com.example.foodle.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ResOpenReqRepo resOpenReqRepo;
    private final RestaurantRepository restaurantRepository;

    // Lấy danh sách tất cả các yêu cầu mở nhà hàng đang chờ phê duyệt
    public List<AdminOpenRequestDto> getAllPendingRequests() {
        List<OpenRequest> openRequests = resOpenReqRepo.findByIsApprovedIsNull();
        return openRequests.stream()
                .map(AdminOpenRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Lấy chi tiết một yêu cầu mở nhà hàng theo requestId
    public AdminOpenRequestDto getOpenRequestById(Long requestId) {
        OpenRequest openRequest = resOpenReqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));
        return AdminOpenRequestDto.fromEntity(openRequest);
    }

    // Phê duyệt yêu cầu mở nhà hàng
    public void approveOpenRequest(Long requestId) {
        OpenRequest openRequest = resOpenReqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));

        if (openRequest.getIsApproved() != null) {
            throw new RuntimeException("Yêu cầu đã được xử lý trước đó.");
        }

        // Cập nhật trạng thái yêu cầu
        openRequest.setIsApproved(true);
        resOpenReqRepo.save(openRequest);

        // Cập nhật trạng thái của nhà hàng
        Restaurant restaurant = openRequest.getRestaurant();
        restaurant.setStatus(Restaurant.Status.APPROVED);  // Nhà hàng được phê duyệt
        restaurantRepository.save(restaurant);
    }

    // Từ chối yêu cầu mở nhà hàng
    public void rejectOpenRequest(Long requestId, String reason) {
        OpenRequest openRequest = resOpenReqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));

        if (openRequest.getIsApproved() != null) {
            throw new RuntimeException("Yêu cầu đã được xử lý trước đó.");
        }

        // Cập nhật trạng thái yêu cầu
        openRequest.setIsApproved(false);
        openRequest.setReason(reason);  // Lưu lý do từ chối
        resOpenReqRepo.save(openRequest);
    }
}
