package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.CustomerDTO;
import com.devak.mrdaebakdinner.dto.OrderDTO;
import com.devak.mrdaebakdinner.dto.OrderResponseDTO;
import com.devak.mrdaebakdinner.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 페이지 GET 요청
    @GetMapping("/customer/orders/new")
    public String showOrderPage(HttpSession session) {
        return "customer/order";
    }

    // 주문 요청
    @PostMapping("/customer/orders/new")
    public String takeOrder(@Valid @ModelAttribute OrderDTO orderDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {

        if (bindingResult.hasErrors()) {
            // 오류 메시지 이어붙이기
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMessage.append(fieldError.getField()).append(": ");
                errorMessage.append(fieldError.getDefaultMessage()).append("<br>");
            });

            // redirectAttributes에 errorMessage 전달
            redirectAttributes.addFlashAttribute("errorMessage",
                    errorMessage.toString().trim());
            return "redirect:/customer/orders/new";
        }

        orderService.placeOrder(orderDTO,
                (CustomerDTO) session.getAttribute("loggedInCustomer"));

        return "redirect:/customer/orders/success";
    }

    @GetMapping("/customer/orders/success")
    public String showOrderSuccess() {
        return "customer/order-success";
    }

    // 이전주문기록 조회 요청
    @GetMapping("/customer/orders/history")
    public String showCustomerOrderHistory(@SessionAttribute("loggedInCustomer") CustomerDTO customer,
                                           Model model) {

        // 고객의 order목록을 찾아서 보여주는 로직
        List<OrderResponseDTO> orderList =
                orderService.findAllByCustomerId(customer.getId());
        // order-history.html에 "orderList"라는 속성으로 전달
        model.addAttribute("orderList", orderList);
        return "customer/order-history";
    }

    // 재주문 요청
    @GetMapping("/customer/order/reorder/{orderId}")
    public String takeReorder(@PathVariable Long orderId, HttpSession session) {
        orderService.placeOrder(orderService.buildReorderDTO(orderId),
                (CustomerDTO) session.getAttribute("loggedInCustomer"));

        return "redirect:/customer/orders/success";
    }


    // TODO: 배달완료 시 deliveryTime set
}
