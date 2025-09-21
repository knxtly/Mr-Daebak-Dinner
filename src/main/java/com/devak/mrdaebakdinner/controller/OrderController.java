package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.*;
import com.devak.mrdaebakdinner.entity.OrderItemId;
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
                orderService.findOrderHistoryByLoginId(customer.getLoginId());
        // "orderList"라는 속성으로 전달
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

    // 주문 상세 요청
    @GetMapping("/orders/detail")
    public String showOrderDetail(@RequestParam Long orderId,
                                  HttpSession session,
                                  Model model) {
        // orderId로부터 OrderHistoryDTO 불러오기
        OrderHistoryDTO order = orderService.findOrderHistoryByOrderId(orderId);
        OrderItemDTO orderItem = orderService.findOrderItemByOrderId(orderId);

        // 세션에서 사용자 확인
        CustomerLoginDTO customer = (CustomerLoginDTO) session.getAttribute("loggedInCustomer");
        ChefStaffDTO chef = (ChefStaffDTO) session.getAttribute("loggedInChef");
        DeliveryStaffDTO delivery = (DeliveryStaffDTO) session.getAttribute("loggedInDelivery");

        // 고객인 경우, 자기 주문인지 체크
        if (customer != null) {
            if (!order.getCustomerLoginId().equals(customer.getLoginId())) {
                throw new IllegalArgumentException("다른 고객의 주문은 조회할 수 없습니다.");
            }
            model.addAttribute("order", order);
            model.addAttribute("orderItem", orderItem);
            return "customer/order-detail-customer"; // 고객용 뷰
        }

        // 직원인 경우
        if (chef != null || delivery != null) {
            model.addAttribute("order", order);
            model.addAttribute("orderItem", orderItem);
            return "staff/order-detail-staff"; // staff용 뷰
        }

        // 로그인 안 됨
        return "redirect:/";
    }

    // TODO: 배달완료 시 deliveryTime set
}
