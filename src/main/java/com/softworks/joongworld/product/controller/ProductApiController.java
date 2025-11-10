package com.softworks.joongworld.product.controller;

import com.softworks.joongworld.product.dto.ProductCreateRequest;
import com.softworks.joongworld.product.dto.ProductDetailView;
import com.softworks.joongworld.product.dto.ProductUpdateRequest;
import com.softworks.joongworld.product.service.ProductService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Slf4j
public class ProductApiController {

    private final ProductService productService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailView> createProduct(@Valid @ModelAttribute ProductCreateRequest request) {
        LoginUserInfo currentUser = getCurrentUser();
        ProductDetailView created = productService.createProduct(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailView> updateProduct(
            @PathVariable Long productId,
            @Valid @ModelAttribute ProductUpdateRequest request) {

        LoginUserInfo currentUser = getCurrentUser();
        log.info("UPDATE REQ: minwoodebug1: {}", request);
        ProductDetailView updated = productService.updateProduct(productId, currentUser, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        LoginUserInfo currentUser = getCurrentUser();
        productService.deleteProduct(productId, currentUser);
        return ResponseEntity.noContent().build();
    }

    private LoginUserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserInfo info && info.getId() != null) {
            return info;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }

}
