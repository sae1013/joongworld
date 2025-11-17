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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminReportApiController {

  private final AdminReportService adminReportService;

  /**
   * 관리자 > 대시보드 신고 내역을 처리 API
   *
   * @param reportId:   신고ID
   * @param request
   * @param currentUser
   * @return
   */
  @PutMapping(value = "/api/admin/reports/{reportId}/resolve")
  public ResponseEntity<Void> resolveReport(@PathVariable Long reportId,
      @Valid @RequestBody ReportResolutionRequest request,
      @CurrentUser LoginUserInfo currentUser) {
    adminReportService.resolveReport(reportId, request, currentUser);
    return ResponseEntity.noContent().build();
  }
}
