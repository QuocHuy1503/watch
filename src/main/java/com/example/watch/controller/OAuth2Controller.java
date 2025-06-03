package com.example.watch.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller để xử lý sau khi OAuth2 login thành công.
 * Nếu đăng nhập thành công, Spring Security set một Authentication chứa OAuth2User.
 */
@Controller
public class OAuth2Controller {

    @GetMapping("/oauth2/success")
    public String oauth2Success(Model model,
                                @AuthenticationPrincipal OAuth2User oauth2User) {
        // Lấy thông tin từ OAuth2User
        String name = oauth2User.getAttribute("name");
        String email = oauth2User.getAttribute("email");
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        return "home"; // Trả về view home.html
    }
}
