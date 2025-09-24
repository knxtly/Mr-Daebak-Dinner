package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.*;
import com.devak.mrdaebakdinner.exception.IncorrectPasswordException;
import com.devak.mrdaebakdinner.service.InventoryService;
import com.devak.mrdaebakdinner.service.OrderService;
import com.devak.mrdaebakdinner.service.StaffService;
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
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final InventoryService inventoryService;
    private final OrderService orderService;

    // Staff 기본화면 (로그인화면)
    @GetMapping("/staff")
    public String showStaffInterface(HttpSession session) {
        // 이미 staff session이 있으면 바로 chef화면으로
        if (session.getAttribute("loggedInStaff") != null) {
            return "redirect:/staff/chef";
        }
        return "staff/staff";
    }

    // Staff login 요청 (PW만 입력)
    @PostMapping("/staff/login")
    public String loginStaff(@Valid @ModelAttribute StaffLoginDTO staffLoginDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {

        // 유효성 검사(@Valid + BindingResult): PW가 입력되지 않았을 때 loginErrorMessage
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("loginErrorMessage",
                    Objects.requireNonNull(bindingResult.getFieldError("password")).getDefaultMessage()
            );
            return "redirect:/staff";
        }

        try {
            // Customer 세션 있으면 삭제
            if (session.getAttribute("loggedInCustomer") != null) {
                session.removeAttribute("loggedInCustomer");
            }
            // 로그인 시도
            StaffSessionDTO staffSessionDTO = staffService.login(staffLoginDTO.getPassword());
            session.setAttribute("loggedInStaff", staffSessionDTO);
            return "redirect:/staff/chef";
        } catch (IncorrectPasswordException e) { // 로그인 실패
            redirectAttributes.addFlashAttribute("loginErrorMessage", e.getMessage());
            return "redirect:/staff";
        }
    }

    // 로그아웃 요청
    @GetMapping("/staff/logout")
    public String staffLogout(HttpSession session) {
        session.removeAttribute("loggedInStaff");
        return "redirect:/staff";
    }

    // Staff - Chef
    @GetMapping("/staff/chef")
    public String showStaffChefInterface(HttpSession session, Model model) {
        // loggedInStaff 세션이 있으면 value를 "chef"로 설정
        if (session.getAttribute("loggedInStaff") != null) {
            session.setAttribute("loggedInStaff", new StaffSessionDTO("chef"));
        }

        // 주문 상태가 '주문완료' 또는 '요리중'인 주문 조회
        List<OrderHistoryDTO> chefOrderHistoryList = orderService.getChefOrders();
        model.addAttribute("chefOrderList", chefOrderHistoryList);

        return "staff/chef";
    }

//    TODO: GET /staff/chef/start(orderId=${order.id})
//    TODO: GET /staff/chef/complete(orderId=${order.id})

    // Staff - Delivery
    @GetMapping("/staff/delivery")
    public String showStaffDeliveryInterface(HttpSession session, Model model) {
        // loggedInStaff 세션이 있으면 value를 "delivery"로 설정
        if (session.getAttribute("loggedInStaff") != null) {
            session.setAttribute("loggedInStaff", new StaffSessionDTO("chef"));
        }

        // 주문 상태가 '배달대기' 또는 '배달중'인 주문 조회
        List<OrderHistoryDTO> deliveryOrders = orderService.getDeliveryOrders();
        model.addAttribute("deliveryOrderList", deliveryOrders);

        // "요리중" 주문 조회
        List<OrderHistoryDTO> cookingOrders = orderService.getCookingOrders();
        model.addAttribute("cookingOrderList", cookingOrders);


        return "staff/delivery";
    }

//    TODO: GET /staff/delivery/start(orderId=${order.id})
//    TODO: GET /staff/delivery/complete(orderId=${order.id})

    // Inventory
    @GetMapping("/staff/inventory")
    public String showInventory(Model model) {
        List<InventoryDTO> inventoryDTOList = inventoryService.findAllInventory();
        model.addAttribute("inventoryList", inventoryDTOList);
        return "staff/inventory";
    }
}
