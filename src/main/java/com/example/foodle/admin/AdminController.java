package com.example.foodle.admin;

import com.example.foodle.admin.dto.AdminOpenRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminOpenRequestService;

    // Lấy danh sách tất cả yêu cầu mở nhà hàng
    @GetMapping("/all")
    public ResponseEntity<List<AdminOpenRequestDto>> getAllPendingOpenRequests() {
        List<AdminOpenRequestDto> pendingRequests = adminOpenRequestService.getAllPendingRequests();
        return ResponseEntity.ok(pendingRequests);
    }

    // Lấy chi tiết một yêu cầu mở nhà hàng
    @GetMapping("/{requestId}")
    public ResponseEntity<AdminOpenRequestDto> getOpenRequestById(@PathVariable Long requestId) {
        AdminOpenRequestDto openRequest = adminOpenRequestService.getOpenRequestById(requestId);
        return ResponseEntity.ok(openRequest);
    }

    // Phê duyệt yêu cầu mở nhà hàng
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<String> approveOpenRequest(@PathVariable Long requestId) {
        try {
            adminOpenRequestService.approveOpenRequest(requestId);
            return ResponseEntity.ok("Yêu cầu đã được phê duyệt.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Từ chối yêu cầu mở nhà hàng
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<String> rejectOpenRequest(@PathVariable Long requestId, @RequestBody String reason) {
        try {
            adminOpenRequestService.rejectOpenRequest(requestId, reason);
            return ResponseEntity.ok("Yêu cầu đã bị từ chối.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
