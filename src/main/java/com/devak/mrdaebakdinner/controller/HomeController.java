package com.devak.mrdaebakdinner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String showIndex() {
        // TODO: 이미 고객 로그인 돼있으면 /customer/main으로
        return "index";
    }
}