package com.softworks.joongworld.product.service;

import com.softworks.joongworld.global.storage.FileStorageService;
import com.softworks.joongworld.global.storage.StorageException;
import com.softworks.joongworld.product.dto.ProductCreateRequest;
import com.softworks.joongworld.product.dto.ProductDetailView;
import com.softworks.joongworld.product.dto.ProductSummaryView;
import com.softworks.joongworld.product.dto.ProductUpdateRequest;
import com.softworks.joongworld.product.repository.ProductCreateParam;
import com.softworks.joongworld.product.repository.ProductMapper;
import com.softworks.joongworld.product.repository.ProductUpdateParam;
import com.softworks.joongworld.user.dto.UserInfoView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;

    public Page<ProductSummaryView> getProductPage(Integer categoryId, Pageable pageable) {
        Pageable effective = normalizePageable(pageable);
        long totalCount = productMapper.countSummaries(categoryId);

        if (totalCount == 0) {
            return new PageImpl<>(List.of(), effective, totalCount);
        }

        if (effective.getOffset() >= totalCount) {
            int lastPageIndex = (int) ((totalCount - 1) / effective.getPageSize());
            effective = PageRequest.of(lastPageIndex, effective.getPageSize(), effective.getSort());
        }

        int offset = (int) effective.getOffset();
        List<ProductSummaryView> items = productMapper.findSummaries(categoryId, effective.getPageSize(), offset);

        return new PageImpl<>(items, effective, totalCount);
    }

    public List<ProductSummaryView> getProductsByUser(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return productMapper.findSummariesByUserId(userId);
    }

    /**
     * 상품 상세정보 리턴
     * @param productId 상품ID
     * @return
     */
    public ProductDetailView getProductDetail(Long productId) {
        ProductDetailView product = productMapper.findDetailById(productId);

        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
        return product;
    }

    @Transactional
    public ProductDetailView createProduct(Long userId, ProductCreateRequest request) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (request.getCategoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리를 선택해 주세요.");
        }

        if (!StringUtils.hasText(request.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품명을 입력해 주세요.");
        }

        if (!StringUtils.hasText(request.getRegion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "거래 지역을 입력해 주세요.");
        }

        if (!StringUtils.hasText(request.getDescription())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 설명을 입력해 주세요.");
        }

        if (!StringUtils.hasText(request.getConditionStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 상태를 선택해 주세요.");
        }

        Long price = request.getPrice();
        if (price == null || price <= -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "판매 가격은 0원 이상이어야 합니다.");
        }

        Long shippingCost = request.getShippingCost();
        if (shippingCost == null || shippingCost < 0) {
            shippingCost = 0L;
        }

        List<MultipartFile> images = request.getImages();

        // 로컬 파일저장경로
        String subFolder = "products/" + userId;

        // 저장된 이미지 로컬 경로의 배열
        List<String> storedPaths = CollectionUtils.isEmpty(images)
                ? List.of()
                : images.stream()
                .filter(Objects::nonNull)
                .filter(file -> !file.isEmpty())
                .map(file -> fileStorageService.store(file, subFolder))
                .collect(Collectors.toList());

        int imageCount = storedPaths.size();
        int thumbnailIndex = request.getThumbnailIndex() != null ? request.getThumbnailIndex() : 0;
        if (thumbnailIndex < 0 || thumbnailIndex >= imageCount) {
            thumbnailIndex = imageCount > 0 ? 0 : -1;
        }

        // 썸네일 이미지는 thumbnailIndex 번 째 이미지로 세팅
        String thumbnailUrl = imageCount > 0 && thumbnailIndex >= 0 ? storedPaths.get(thumbnailIndex) : null;

        ProductCreateParam param = new ProductCreateParam();

        param.setCategoryId(request.getCategoryId());
        param.setUserId(userId);
        param.setTitle(request.getTitle());
        param.setPrice(price);
        param.setRegion(request.getRegion());
        param.setSafePay(Boolean.TRUE.equals(request.getSafePay()));
        param.setShippingAvailable(Boolean.TRUE.equals(request.getShippingAvailable()));
        param.setMeetupAvailable(Boolean.TRUE.equals(request.getMeetupAvailable()));
        param.setShippingCost(shippingCost);
        param.setConditionStatus(request.getConditionStatus());
        param.setDescription(request.getDescription());
        param.setThumbnailUrl(thumbnailUrl);
        param.setImageUrls(storedPaths);
        param.setThumbnailIndex(thumbnailIndex >= 0 ? thumbnailIndex : null);
        param.setImageCount(imageCount);

        // TODO: 상품을 새로 생성할 때, 수정할 때 분기처리해서 작업할 것.
        productMapper.insertProduct(param);

        return getProductDetail(param.getId());
    }

    @Transactional
    public ProductDetailView updateProduct(Long productId, Long userId, ProductUpdateRequest request) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 ID가 필요합니다.");
        }

        ProductDetailView current = productMapper.findDetailById(productId);
        if (current == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
        if (current.getUserInfo() == null || current.getUserInfo().getId() == null
                || !userId.equals(current.getUserInfo().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 상품만 수정할 수 있습니다.");
        }

        if (request.getCategoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리를 선택해 주세요.");
        }
        if (!StringUtils.hasText(request.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품명을 입력해 주세요.");
        }
        if (!StringUtils.hasText(request.getRegion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "거래 지역을 입력해 주세요.");
        }
        if (!StringUtils.hasText(request.getDescription())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 설명을 입력해 주세요.");
        }
        if (!StringUtils.hasText(request.getConditionStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 상태를 선택해 주세요.");
        }

        Long price = request.getPrice();
        if (price == null || price <= -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "판매 가격은 0원 이상이어야 합니다.");
        }

        boolean shippingAvailable = Boolean.TRUE.equals(request.getShippingAvailable());
        Long shippingCost = request.getShippingCost();
        if (!shippingAvailable) {
            shippingCost = 0L;
        } else if (shippingCost == null || shippingCost < 0) {
            shippingCost = 0L;
        }

        List<String> currentImages = current.getImages() != null
                ? new ArrayList<>(current.getImages())
                : new ArrayList<>();

        List<Integer> removedIndexes = request.getRemovedImages() != null
                ? new ArrayList<>(request.getRemovedImages())
                : new ArrayList<>();

        removedIndexes.removeIf(Objects::isNull);
        removedIndexes = removedIndexes.stream()
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
        removedIndexes.sort(Comparator.reverseOrder());

        for (Integer index : removedIndexes) {
            if (index < 0 || index >= currentImages.size()) {
                continue;
            }
            String removedPath = currentImages.remove((int) index);
            fileStorageService.delete(removedPath);
        }

        List<MultipartFile> newImages = request.getImages();
        String subFolder = "products/" + userId;
        if (!CollectionUtils.isEmpty(newImages)) {
            newImages.stream()
                    .filter(Objects::nonNull)
                    .filter(file -> !file.isEmpty())
                    .map(file -> fileStorageService.store(file, subFolder))
                    .forEach(currentImages::add);
        }

        int imageCount = currentImages.size();
        int thumbnailIndex = request.getThumbnailIndex() != null ? request.getThumbnailIndex() : 0;
        if (thumbnailIndex < 0 || thumbnailIndex >= imageCount) {
            thumbnailIndex = imageCount > 0 ? 0 : -1;
        }
        String thumbnailUrl = imageCount > 0 && thumbnailIndex >= 0 ? currentImages.get(thumbnailIndex) : null;

        ProductUpdateParam param = new ProductUpdateParam();
        param.setId(productId);
        param.setUserId(userId);
        param.setCategoryId(request.getCategoryId());
        param.setTitle(request.getTitle());
        param.setPrice(price);
        param.setRegion(request.getRegion());
        param.setSafePay(Boolean.TRUE.equals(request.getSafePay()));
        param.setShippingAvailable(shippingAvailable);
        param.setMeetupAvailable(Boolean.TRUE.equals(request.getMeetupAvailable()));
        param.setShippingCost(shippingCost);
        param.setConditionStatus(request.getConditionStatus());
        param.setDescription(request.getDescription());
        param.setThumbnailUrl(thumbnailUrl);
        param.setImageUrls(currentImages);
        param.setThumbnailIndex(thumbnailIndex >= 0 ? thumbnailIndex : null);
        param.setImageCount(imageCount);

        int updated = productMapper.updateProduct(param);
        if (updated < 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상품 수정에 실패했습니다.");
        }

        return getProductDetail(productId);
    }

    @Transactional
    public void deleteProduct(Long productId, Long requestUserId) {
        if (requestUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 상품 ID를 입력해 주세요.");
        }

        UserInfoView owner = productMapper.findProductOwner(productId);
        if (owner == null || owner.getId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.");
        }

        if (!requestUserId.equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 상품만 삭제할 수 있습니다.");
        }

        int deletedRows = productMapper.deleteProduct(productId);
        if (deletedRows < 1) {
            throw new StorageException("상품 삭제에 실패했습니다.");
        }
    }

    private Pageable normalizePageable(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        int pageNumber = Math.max(pageable.getPageNumber(), 0);
        int pageSize = pageable.getPageSize();
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        } else if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
