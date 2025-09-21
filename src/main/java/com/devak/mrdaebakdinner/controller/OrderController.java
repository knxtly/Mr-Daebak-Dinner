package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.CustomerLoginDTO;
import com.devak.mrdaebakdinner.dto.OrderDTO;
import com.devak.mrdaebakdinner.dto.OrderHistoryDTO;
import com.devak.mrdaebakdinner.dto.OrderItemDTO;
import com.devak.mrdaebakdinner.service.OrderService;
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

    // Customer: 주문 페이지 GET 요청
    @GetMapping("/customer/orders/new")
    public String showOrderPage() {
        return "customer/order";
    }

    // 주문 요청
    @PostMapping("/customer/orders/new")
    public String takeOrder(@Valid @ModelAttribute OrderDTO orderDTO,
                            BindingResult bindingResult,
                            @ModelAttribute OrderItemDTO orderItemDTO,
                            RedirectAttributes redirectAttributes,
                            @SessionAttribute("loggedInCustomer") CustomerLoginDTO customerLoginDTO) {

        if (bindingResult.hasErrors()) { // Valid체크에서 오류가 있을 시, 에러메시지 전달
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMessage.append(fieldError.getDefaultMessage()).append("<br>");
            });
            redirectAttributes.addFlashAttribute("errorMessage",
                    errorMessage.toString().trim());
            return "redirect:/customer/orders/new";
        }

        orderService.placeOrder(orderDTO, orderItemDTO, customerLoginDTO);

        return "redirect:/customer/orders/success";
    }

    @GetMapping("/customer/orders/success")
    public String showOrderSuccess() {
        return "customer/order-success";
    }

    // 이전주문기록 조회 요청
    @GetMapping("/customer/orders/history")
    public String showCustomerOrderHistory(@SessionAttribute("loggedInCustomer") CustomerLoginDTO customer,
                                           Model model) {

        // 고객의 loginId로 order목록을 찾아서 보여주는 로직
        List<OrderHistoryDTO> orderList =
                orderService.findAllByLoginId(customer.getLoginId());
        // order-history.html에 "orderList"라는 속성으로 전달
        model.addAttribute("orderList", orderList);
        return "customer/order-history";
    }

    // 재주문 요청
    @GetMapping("/customer/order/reorder/{orderId}")
    public String takeReorder(@PathVariable Long orderId,
                              @SessionAttribute("loggedInCustomer") CustomerLoginDTO customerLoginDTO) {

        orderService.placeOrder(
                orderService.buildOrderDTO(orderId),
                orderService.buildOrderItemDTO(orderId),
                customerLoginDTO
        );

        return "redirect:/customer/orders/success";
    }


    // TODO: 배달완료 시 deliveryTime set
}
