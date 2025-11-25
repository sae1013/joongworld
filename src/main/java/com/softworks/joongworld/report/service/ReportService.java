package com.softworks.joongworld.report.service;

import com.softworks.joongworld.consts.enums.ReportStatus;
import com.softworks.joongworld.consts.enums.ReportTargetType;
import com.softworks.joongworld.product.repository.ProductMapper;
import com.softworks.joongworld.report.dto.ReportRequest;
import com.softworks.joongworld.report.dto.ReportResponse;
import com.softworks.joongworld.report.repository.ReportInsertParam;
import com.softworks.joongworld.report.repository.ReportMapper;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import com.softworks.joongworld.user.dto.UserInfoView;
import com.softworks.joongworld.user.dto.UserResponse;
import com.softworks.joongworld.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportMapper reportMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    @Transactional
    public ReportResponse reportProduct(Long productId,
                                        ReportRequest request,
                                        LoginUserInfo currentUser) {
        LoginUserInfo reporter = ensureLoggedIn(currentUser);
        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신고할 상품을 선택해 주세요.");
        }

        UserInfoView owner = productMapper.findProductOwner(productId);
        if (owner == null || owner.getId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다.");
        }

        if (owner.getId().equals(reporter.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인의 상품은 신고할 수 없습니다.");
        }

        String reasonCode = normalizeReasonCode(request.getReasonCode());
        String description = normalizeDescription(request.getDescription());

        ReportInsertParam param = ReportInsertParam.builder()
            .reporterId(reporter.getId())
            .reportedUserId(owner.getId())
            .reportedProductId(productId)
            .targetType(ReportTargetType.PRODUCT)
            .reasonCode(reasonCode)
            .description(description)
            .status(ReportStatus.PENDING)
            .build();

        int inserted = reportMapper.insertReport(param);
        if (inserted != 1 || param.getId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "신고 저장에 실패했습니다.");
        }

        return ReportResponse.builder()
            .id(param.getId())
            .targetType(ReportTargetType.PRODUCT)
            .targetId(productId)
            .status(ReportStatus.PENDING)
            .reasonCode(reasonCode)
            .description(description)
            .build();
    }

    @Transactional
    public ReportResponse reportUser(Long userId,
                                     ReportRequest request,
                                     LoginUserInfo currentUser) {
        LoginUserInfo reporter = ensureLoggedIn(currentUser);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신고할 사용자를 선택해 주세요.");
        }

        UserResponse targetUser = userMapper.findById(userId);
        if (targetUser == null || targetUser.getId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");
        }
        if (userId.equals(reporter.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인을 신고할 수 없습니다.");
        }

        String reasonCode = normalizeReasonCode(request.getReasonCode());
        String description = normalizeDescription(request.getDescription());

        ReportInsertParam param = ReportInsertParam.builder()
            .reporterId(reporter.getId())
            .reportedUserId(userId)
            .targetType(ReportTargetType.USER)
            .reasonCode(reasonCode)
            .description(description)
            .status(ReportStatus.PENDING)
            .build();

        int inserted = reportMapper.insertReport(param);
        if (inserted != 1 || param.getId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "신고 저장에 실패했습니다.");
        }

        return ReportResponse.builder()
            .id(param.getId())
            .targetType(ReportTargetType.USER)
            .targetId(userId)
            .status(ReportStatus.PENDING)
            .reasonCode(reasonCode)
            .description(description)
            .build();
    }

    private LoginUserInfo ensureLoggedIn(LoginUserInfo currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return currentUser;
    }

    private String normalizeReasonCode(String reasonCode) {
        if (!StringUtils.hasText(reasonCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신고 사유를 선택해 주세요.");
        }
        return reasonCode.trim().toUpperCase();
    }

    private String normalizeDescription(String description) {
        if (!StringUtils.hasText(description)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신고 내용을 입력해 주세요.");
        }
        String normalized = description.trim();
        if (normalized.length() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신고 내용은 1000자 이하로 작성해 주세요.");
        }
        return normalized;
    }
}
