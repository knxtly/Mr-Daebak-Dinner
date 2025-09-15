package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.OrderDTO;
import com.devak.mrdaebakdinner.entity.CustomerEntity;
import com.devak.mrdaebakdinner.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 페이지 GET 요청
    @GetMapping("/customer/order")
    public String showOrderPage(HttpSession session) {
        CustomerEntity loggedInCustomer =
                (CustomerEntity) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            return "redirect:/customer"; // 로그인 페이지로 이동
        }
        return "customer/order";
    }

    // 주문(POST) 요청
    @PostMapping("/customer/order/new")
    public String takeOrder(@Valid @ModelAttribute OrderDTO orderDTO,
                            BindingResult bindingResult) {
        // TODO: 재고가 충분하면 주문허가하는 로직

        return "redirect:/customer/order/success";
    }

    @GetMapping("/customer/order/success")
    public String showOrderSuccess() {
        return "customer/order-success";
    }

    // 이전주문기록 페이지 GET 요청
    @GetMapping("/customer/order/history")
    public String showCustomerOrderHistory(HttpSession session) {
        // TODO: List에 order목록 담아서 보냄
        List<OrderDTO> orderDTOList = orderService.findAllByCustomerId(
                (CustomerEntity) session.getAttribute("loggedInCustomer"));
        return "customer/order-history";
    }

    // TODO: 배달완료 시 deliveryTime set작업
}
