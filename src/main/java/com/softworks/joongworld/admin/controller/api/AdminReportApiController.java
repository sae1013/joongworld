package com.softworks.joongworld.admin.controller.api;

import com.softworks.joongworld.admin.dto.ReportResolutionRequest;
import com.softworks.joongworld.admin.service.AdminReportService;
import com.softworks.joongworld.auth.support.CurrentUser;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reports")
public class AdminReportApiController {

    private final AdminReportService adminReportService;

    @PutMapping("/{reportId}/resolve")
    public ResponseEntity<Void> resolveReport(@PathVariable Long reportId,
                                              @Valid @RequestBody ReportResolutionRequest request,
                                              @CurrentUser LoginUserInfo currentUser) {
        adminReportService.resolveReport(reportId, request, currentUser);
        return ResponseEntity.noContent().build();
    }
}
