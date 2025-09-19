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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 페이지 GET 요청
    @GetMapping("/customer/orders/new")
    public String showOrderPage(HttpSession session) {
        // 로그인한 customer session이 없으면 로그인 페이지로 이동
        // TODO: Interceptor로 나중에 바꿔보기
        if (session.getAttribute("loggedInCustomer") == null) {
            return "redirect:/customer";
        }

        return "customer/order";
    }

    // 주문 요청
    @PostMapping("/customer/orders/new")
    public String takeOrder(@Valid @ModelAttribute OrderDTO orderDTO,
                            BindingResult bindingResult,
                            HttpSession session) {
        // 로그인한 customer session이 없으면 로그인 페이지로 이동
        // TODO: Interceptor로 나중에 바꿔보기
        if (session.getAttribute("loggedInCustomer") == null) {
            return "redirect:/customer";
        }

        if (bindingResult.hasErrors()) {
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
    public String showCustomerOrderHistory(HttpSession session, Model model) {
        // 로그인한 customer session이 없으면 로그인 페이지로 이동
        // TODO: Interceptor로 나중에 바꿔보기
        if (session.getAttribute("loggedInCustomer") == null) {
            return "redirect:/customer";
        }

        // 고객의 order목록을 찾아서 보여주는 로직
        List<OrderResponseDTO> orderList = orderService.findAllByCustomerId(
                ((CustomerDTO) session.getAttribute("loggedInCustomer")).getId());
        // order-history.html에 "orderList"라는 속성으로 전달
        model.addAttribute("orderList", orderList);
        return "customer/order-history";
    }

    // 재주문 요청
    @GetMapping("/customer/order/reorder/{orderId}")
    public String takeReorder(@PathVariable Long orderId, HttpSession session) {
        // 로그인한 customer session이 없으면 로그인 페이지로 이동
        // TODO: Interceptor로 나중에 바꿔보기
        if (session.getAttribute("loggedInCustomer") == null) {
            return "redirect:/customer";
        }

        orderService.placeOrder(orderService.buildReorderDTO(orderId),
                (CustomerDTO) session.getAttribute("loggedInCustomer"));

        return "redirect:/customer/orders/success";
    }


    // TODO: 배달완료 시 deliveryTime set
}
