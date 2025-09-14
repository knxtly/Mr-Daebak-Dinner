package com.devak.mrdaebakdinner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffController {
    @GetMapping("/staff")
    public String showStaffInterface() {
        return "staff";
    }
}
