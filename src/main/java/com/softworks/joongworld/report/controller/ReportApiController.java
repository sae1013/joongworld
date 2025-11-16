package com.softworks.joongworld.report.controller;

import com.softworks.joongworld.auth.support.CurrentUser;
import com.softworks.joongworld.report.dto.ReportRequest;
import com.softworks.joongworld.report.dto.ReportResponse;
import com.softworks.joongworld.report.service.ReportService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportApiController {

    private final ReportService reportService;

    @PostMapping("/api/products/{productId}/reports")
    public ResponseEntity<ReportResponse> reportProduct(@PathVariable Long productId,
                                                        @Valid @RequestBody ReportRequest request,
                                                        @CurrentUser LoginUserInfo currentUser) {
        ReportResponse response = reportService.reportProduct(productId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/users/{userId}/reports")
    public ResponseEntity<ReportResponse> reportUser(@PathVariable Long userId,
                                                     @Valid @RequestBody ReportRequest request,
                                                     @CurrentUser LoginUserInfo currentUser) {
        ReportResponse response = reportService.reportUser(userId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
