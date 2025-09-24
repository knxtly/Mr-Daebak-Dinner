package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.InventoryDTO;
import com.devak.mrdaebakdinner.dto.StaffLoginDTO;
import com.devak.mrdaebakdinner.dto.StaffSessionDTO;
import com.devak.mrdaebakdinner.exception.IncorrectPasswordException;
import com.devak.mrdaebakdinner.service.InventoryService;
import com.devak.mrdaebakdinner.service.StaffService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final InventoryService inventoryService;

    // Staff 기본화면 (로그인화면)
    @GetMapping("/staff")
    public String showStaffInterface(HttpSession session) {
        // 이미 staff session이 있으면 바로 chef화면으로
        Object staffRole = session.getAttribute("loggedInStaff");

        if ("chef".equals(staffRole) || "delivery".equals(staffRole)) {
            return "redirect:/staff/chef";
        }
        return "staff/staff";
    }

    // Staff login 요청 (PW만 입력)
    @PostMapping("/staff/login")
    public String loginStaff(@Valid @ModelAttribute StaffLoginDTO staffLoginDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             HttpSession session,
                             HttpServletRequest request) {
        // 유효성 검사(@Valid + BindingResult): PW가 입력되지 않았을 때 loginErrorMessage
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("loginErrorMessage",
                    bindingResult.getFieldError("password").getDefaultMessage()
            );
            return "redirect:/staff";
        }

        try {
            StaffSessionDTO sessionDTO = staffService.login(staffLoginDTO.getPassword());

            session.invalidate(); // 기존 세션 초기화
            HttpSession newSession = request.getSession(true); // 새 세션 발급

            newSession.setAttribute("loggedInStaff", sessionDTO);
            return "redirect:/staff/chef";
        } catch (IncorrectPasswordException e) { // 로그인 실패
            redirectAttributes.addFlashAttribute("loginErrorMessage", e.getMessage());
            return "redirect:/staff";
        }
    }

    // Staff - Chef
    @GetMapping("/staff/chef")
    public String showStaffChefInterface() {
        return "staff/chef";
    }

    // Staff - Delivery
    @GetMapping("/staff/delivery")
    public String showStaffDeliveryInterface() {
        return "staff/delivery";
    }

    // Staff - Delivery
    @GetMapping("/staff/inventory")
    public String showInventory(Model model) {
        List<InventoryDTO> inventoryDTOList = inventoryService.findAllInventory();
        model.addAttribute("inventoryList", inventoryDTOList);
        return "staff/inventory";
    }
}
