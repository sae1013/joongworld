package com.softworks.joongworld.product.controller;

import com.softworks.joongworld.product.dto.ProductCreateRequest;
import com.softworks.joongworld.product.dto.ProductDetailView;
import com.softworks.joongworld.product.service.ProductService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Slf4j
public class ProductApiController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailView> createProduct(@RequestParam("title") String title,
                                                           @RequestParam("price") Long price,
                                                           @RequestParam("region") String region,
                                                           @RequestParam("condition_status") String conditionStatus,
                                                           @RequestParam("description") String description,
                                                           @RequestParam(value = "safe_pay", defaultValue = "false") boolean safePay,
                                                           @RequestParam(value = "shipping_available", defaultValue = "false") boolean shippingAvailable,
                                                           @RequestParam(value = "meetup_available", defaultValue = "false") boolean meetupAvailable,
                                                           @RequestParam(value = "shipping_cost", defaultValue = "0") Long shippingCost,
                                                           @RequestParam("categoryId") Integer categoryId,
                                                           @RequestParam(value = "thumbnail_index", defaultValue = "0") Integer thumbnailIndex,
                                                           @RequestParam(value = "image_count", defaultValue = "0") Integer imageCount,
                                                           @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        LoginUserInfo currentUser = getCurrentUser();

        ProductCreateRequest request = new ProductCreateRequest();
        request.setTitle(title);
        request.setPrice(price);
        request.setRegion(region);
        request.setConditionStatus(conditionStatus);
        request.setDescription(description);
        request.setSafePay(safePay);
        request.setShippingAvailable(shippingAvailable);
        request.setMeetupAvailable(meetupAvailable);
        request.setShippingCost(shippingCost);
        request.setCategoryId(categoryId);
        request.setThumbnailIndex(thumbnailIndex);
        request.setImageCount(imageCount);
        request.setImages(images);

        ProductDetailView created = productService.createProduct(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    private LoginUserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserInfo info && info.getId() != null) {
            return info;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }
}
