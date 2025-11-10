package com.softworks.joongworld.my.controller;

import com.softworks.joongworld.auth.support.SessionAuthenticationFilter;
import com.softworks.joongworld.common.pageable.Pageables;
import com.softworks.joongworld.product.service.ProductService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MyPageViewController {

    private final ProductService productService;

    @GetMapping("/my")
    public ModelAndView myPage(HttpServletRequest request,
                               RedirectAttributes redirectAttributes,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "size", defaultValue = "" + Pageables.DEFAULT_PAGE_SIZE) int size) {
        log.debug("마이페이지 접근: requestURI={}, contextPath={}", request.getRequestURI(), request.getContextPath());
        Object principal = request.getAttribute(SessionAuthenticationFilter.CURRENT_USER_ATTR);
        LoginUserInfo user = principal instanceof LoginUserInfo info ? info : null;
        if (user == null || user.getId() == null) {
            redirectAttributes.addFlashAttribute("redirectMessage", "로그인이 필요합니다.");
            return new ModelAndView("redirect:/auth/login");
        }

        Pageable pageable = Pageables.from(page, size);
        var myProductsPage = productService.getProductsByUser(user.getId(), pageable);

        ModelAndView mav = new ModelAndView("my/index");
        mav.addObject("nickname", user.getNickname());
        mav.addObject("email", user.getEmail());
        mav.addObject("myProducts", myProductsPage.getItems());
        mav.addObject("page", myProductsPage);
        return mav;
    }
}
