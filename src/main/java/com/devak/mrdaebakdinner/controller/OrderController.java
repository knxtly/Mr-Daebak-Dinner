package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.OrderDTO;
import com.devak.mrdaebakdinner.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 페이지 GET 요청
    @GetMapping("/customer/order")
    public String showOrderPage() {
        return "customer/order";
    }

    // 주문 페이지 POST 요청
    @PostMapping("/customer/order")
    public String takeOrder(@Valid @ModelAttribute OrderDTO orderDTO,
                            BindingResult bindingResult) {

        return "redirect:/customer/order/success";
    }

    // 이전주문기록 페이지 GET 요청
    @GetMapping("/customer/order/history")
    public String showCustomerOrderHistory() {
        return "customer/order-history";
    }
}
