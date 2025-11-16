package com.softworks.joongworld.admin.service;

import com.softworks.joongworld.admin.dto.AdminProductReportView;
import com.softworks.joongworld.admin.dto.AdminUserReportView;
import com.softworks.joongworld.admin.dto.ReportResolutionRequest;
import com.softworks.joongworld.admin.repository.AdminReportMapper;
import com.softworks.joongworld.consts.enums.ProductStatus;
import com.softworks.joongworld.consts.enums.ReportResolutionType;
import com.softworks.joongworld.consts.enums.ReportStatus;
import com.softworks.joongworld.consts.enums.ReportTargetType;
import com.softworks.joongworld.consts.enums.UserStatus;
import com.softworks.joongworld.product.repository.ProductMapper;
import com.softworks.joongworld.report.model.Report;
import com.softworks.joongworld.report.repository.ReportMapper;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import com.softworks.joongworld.user.repository.UserMapper;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private static final int DEFAULT_LIMIT = 50;

    private final AdminReportMapper adminReportMapper;
    private final ReportMapper reportMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    public List<AdminUserReportView> getRecentUserReports() {
        return adminReportMapper.findRecentUserReports(DEFAULT_LIMIT);
    }

    public List<AdminProductReportView> getRecentProductReports() {
        return adminReportMapper.findRecentProductReports(DEFAULT_LIMIT);
    }

    @Transactional
    public void resolveReport(Long reportId,
                              ReportResolutionRequest request,
                              LoginUserInfo currentUser) {
        LoginUserInfo admin = ensureAdmin(currentUser);
        Report report = reportMapper.findById(reportId);
        if (report == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "신고 정보를 찾을 수 없습니다.");
        }

        ReportStatus newStatus = request.getReportStatus();
        if (newStatus == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신고 처리 상태를 선택해 주세요.");
        }

        if (report.getTargetType() == ReportTargetType.USER) {
            applyUserStatus(report, request.getTargetStatus());
        } else if (report.getTargetType() == ReportTargetType.PRODUCT) {
            applyProductStatus(report, request.getTargetStatus());
        }

        ReportResolutionType resolutionType = request.getResolutionType();
        String memo = request.getMemo();

        int updated = reportMapper.updateResolution(report.getId(),
            newStatus,
            admin.getId(),
            resolutionType,
            memo,
            OffsetDateTime.now());
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "신고 상태를 갱신하지 못했습니다.");
        }
    }

    private void applyUserStatus(Report report, String targetStatus) {
        if (!StringUtils.hasText(targetStatus) || report.getReportedUserId() == null) {
            return;
        }
        UserStatus status = UserStatus.from(targetStatus);
        int updated = userMapper.updateStatus(report.getReportedUserId(), status.name());
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 상태를 갱신하지 못했습니다.");
        }
    }

    private void applyProductStatus(Report report, String targetStatus) {
        if (!StringUtils.hasText(targetStatus) || report.getReportedProductId() == null) {
            return;
        }
        ProductStatus status = ProductStatus.from(targetStatus);
        int updated = productMapper.updateProductStatus(report.getReportedProductId(), status.name());
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상품 상태를 갱신하지 못했습니다.");
        }
    }

    private LoginUserInfo ensureAdmin(LoginUserInfo currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (!currentUser.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다.");
        }
        return currentUser;
    }
}
