package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.CustomerLoginDTO;
import com.devak.mrdaebakdinner.dto.StaffLoginDTO;
import com.devak.mrdaebakdinner.exception.CustomerNotFoundException;
import com.devak.mrdaebakdinner.exception.IncorrectPasswordException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StaffController {
    // Staff 기본화면 (로그인화면)
    @GetMapping("/staff")
    public String showStaffInterface() {
        return "staff/staff";
    }

    // Staff login 요청 (PW만 입력)
    @PostMapping("/staff/login")
    public String loginStaff(@Valid @ModelAttribute StaffLoginDTO staffLoginDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        // 유효성 검사(@Valid + BindingResult): ID, PW가 입력되지 않았을 때 loginErrorMessage
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            if (bindingResult.getFieldError("password") != null) {
                redirectAttributes.addFlashAttribute("loginErrorMessage","PW는 필수 요소입니다.");
            }
            return "redirect:/staff";
        }

        if (staffLoginDTO.getPassword().equals("staff")) {
            return "redirect:/staff/chef";
        } else {
            redirectAttributes.addFlashAttribute("loginErrorMessage", "비밀번호가 일치하지 않습니다.");
            return "redirect:/staff";
        }
//        try {
//            CustomerLoginDTO loginResult = customerService.login(customerLoginDTO);
//            // 로그인 성공: 세션에 사용자 정보 저장
//            session.setAttribute("loggedInCustomer", loginResult);
//            return "redirect:/customer/main";
//        } catch (IncorrectPasswordException | CustomerNotFoundException e) {
//            // 로그인 실패: 에러메시지 전달
//            redirectAttributes.addFlashAttribute("loginErrorMessage", e.getMessage());
//            return "redirect:/customer";
//        }
    }

    // Staff - Chef
    @GetMapping("/staff/chef")
    public String showChefInterface() {
        // TODO: session 확인
        return "staff/chef";
    }

    // TODO: Staff - Delivery
    // TODO: show Inventory

}
