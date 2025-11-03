package com.softworks.joongworld.my.controller;

import com.softworks.joongworld.auth.support.SessionAuthenticationFilter;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class MyPageController {

    @GetMapping("/my")
    public String myPage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        log.debug("마이페이지 접근: requestURI={}, contextPath={}", request.getRequestURI(), request.getContextPath());
        Object principal = request.getAttribute(SessionAuthenticationFilter.CURRENT_USER_ATTR);
        LoginUserInfo user = principal instanceof LoginUserInfo info ? info : null;
        if (user == null || user.getId() == null) {
            redirectAttributes.addFlashAttribute("redirectMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("email", user.getEmail());
        return "my/index";
    }
}
