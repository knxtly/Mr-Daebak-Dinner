package com.devak.mrdaebakdinner.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String showIndex(HttpSession session) {
        // 이미 고객 로그인 세션 있으면 /customer/main으로
        if (session.getAttribute("loggedInCustomer") != null) {
            return "redirect:/customer/main";
        }
        return "index";
    }
}