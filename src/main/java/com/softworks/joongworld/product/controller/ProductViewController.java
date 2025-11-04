package com.softworks.joongworld.product.controller;

import com.softworks.joongworld.auth.support.SessionAuthenticationFilter;
import com.softworks.joongworld.category.service.CategoryService;
import com.softworks.joongworld.product.dto.ProductDetailView;
import com.softworks.joongworld.product.service.ProductService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductViewController {
    private final ProductService productService;
    private final CategoryService categoryService;

    /**
     * 상품 게시글 조회
     *
     * @param productId 상품 ID
     * @return View
    */
    @GetMapping("/product/{productId}")
    public ModelAndView detail(@PathVariable Long productId) {
        ModelAndView mav = new ModelAndView("product/detail");
        ProductDetailView product = productService.getProductDetail(productId);
        log.info("상품 상세 조회 productId={}, product={}", productId, product);
        mav.addObject("product", product);
        return mav;
    }

    /**
     * 상품 게시글 수정하기
     * @param productId 상품 ID
     * @return
     */
    @GetMapping("/product/{productId}/edit")
    public ModelAndView edit(@PathVariable Long productId,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        LoginUserInfo currentUser = requireLogin(request, redirectAttributes);

        // 미로그인 유저 진입막기
        if (currentUser == null) {
            return new ModelAndView("redirect:/auth/login");
        }

        // 다른 사람의 글 수정 막고 상품 정보 페이지로 이동
        ProductDetailView product = productService.getProductDetail(productId);
        if (!isOwnerOf(product, currentUser)) {
            return new ModelAndView("redirect:/product/" + productId);
        }

        ModelAndView mav = new ModelAndView("product/new");
        mav.addObject("product", product);
        mav.addObject("isEdit", true);
        mav.addObject("categories", categoryService.getAllCategories());
        return mav;
    }

    /**
     * 판매글 작성하기
     *
     * @return View
     */
    @GetMapping("/product/new")
    public ModelAndView productNew(HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        LoginUserInfo currentUser = requireLogin(request, redirectAttributes);
        if (currentUser == null) {
            return new ModelAndView("redirect:/auth/login");
        }

        ModelAndView mav = new ModelAndView("product/new");
        mav.addObject("isEdit", false);
        mav.addObject("categories", categoryService.getAllCategories());
        return mav;
    }


    // TODO: 컨트롤러에서 검증으로 쓰는 유틸성 함수들은 모두 공통으로 빼기. (Guard, Service Layer?)
    /**
     *  로그인 필수 GUARD
     * @param request
     * @param redirectAttributes
     * @return
     */
    private LoginUserInfo requireLogin(HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        // 요청에서 로그인한 유저객체 추출
        LoginUserInfo loginUser = extractLoginUser(request);
        if (loginUser == null || loginUser.getId() == null) {
            redirectAttributes.addFlashAttribute("AuthMessage", "로그인이 필요합니다.");
            return null;
        }
        return loginUser;
    }

    /**
     * 로그인한 유저정보 객체 추출
     * @param request
     * @return
     */
    private LoginUserInfo extractLoginUser(HttpServletRequest request) {
        Object principal = request.getAttribute(SessionAuthenticationFilter.CURRENT_USER_ATTR);
        if (principal instanceof LoginUserInfo info) {
            return info;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserInfo info) {
            return info;
        }
        return LoginUserInfo.empty();
    }

    /**
     * 상품 게시자가 본인인지 여부 체크
     * @param product 상품객체
     * @param currentUser 현재로그인한 유저
     * @return
     */
    private boolean isOwnerOf(ProductDetailView product, LoginUserInfo currentUser) {
        if (product == null || product.getUserInfo() == null || currentUser == null || currentUser.getId() == null) {
            return false;
        }
        return product.getUserInfo().getId() != null
                && product.getUserInfo().getId().equals(currentUser.getId());
    }
}
