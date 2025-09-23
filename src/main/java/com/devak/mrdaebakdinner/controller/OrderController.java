package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.*;
import com.devak.mrdaebakdinner.exception.InsufficientInventoryException;
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
                            @SessionAttribute("loggedInCustomer") CustomerSessionDTO customerSessionDTO) {

        if (bindingResult.hasErrors()) { // Valid체크에서 오류가 있을 시, 에러메시지 전달
            StringBuilder orderErrorMsg = new StringBuilder();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                orderErrorMsg.append(fieldError.getDefaultMessage()).append("<br>");
            });
            redirectAttributes.addFlashAttribute("orderErrorMessage",
                    orderErrorMsg.toString().trim());
            return "redirect:/customer/orders/new";
        }

        try {
            orderService.placeOrder(orderDTO, orderItemDTO, customerSessionDTO);
        } catch (InsufficientInventoryException e) {
            redirectAttributes.addFlashAttribute("itemErrorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("insufficientItems", e.getInsufficientItems());
            return "redirect:/customer/orders/new";
        }
        return "redirect:/customer/orders/success";
    }

    @GetMapping("/customer/orders/success")
    public String showOrderSuccess() {
        return "customer/order-success";
    }

    // 이전주문기록 조회 요청
    @GetMapping("/customer/orders/history")
    public String showCustomerOrderHistory(@SessionAttribute("loggedInCustomer") CustomerSessionDTO customerSessionDTO,
                                           Model model) {
        // 고객의 loginId로 order목록을 찾아서 보여주는 로직
        List<OrderHistoryDTO> orderList =
                orderService.findOrderHistoryByLoginId(customerSessionDTO.getLoginId());
        // "orderList"라는 속성으로 전달
        model.addAttribute("orderList", orderList);
        return "customer/order-history";
    }

    // 재주문 요청
    @GetMapping("/customer/order/reorder/{orderId}")
    public String takeReorder(@PathVariable Long orderId,
                              RedirectAttributes redirectAttributes,
                              @SessionAttribute("loggedInCustomer") CustomerSessionDTO customerSessionDTO) {
        try {
            orderService.placeOrder(
                    orderService.buildOrderDTO(orderId),
                    orderService.buildOrderItemDTO(orderId),
                    customerSessionDTO
            );
        } catch (InsufficientInventoryException e) {
            redirectAttributes.addFlashAttribute("itemErrorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("insufficientItems", e.getInsufficientItems());
            return "redirect:/customer/orders/new";
        }
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
        CustomerSessionDTO customer = (CustomerSessionDTO) session.getAttribute("loggedInCustomer");
        String staffType = (String) session.getAttribute("loggedInStaff");

        // 고객인 경우
        if (customer != null) {
            // 자기 주문인지 체크
            if (!order.getCustomerLoginId().equals(customer.getLoginId())) {
                throw new IllegalArgumentException("다른 고객의 주문은 조회할 수 없습니다.");
            }
            model.addAttribute("order", order);
            model.addAttribute("orderItem", orderItem);
            return "customer/order-detail-customer"; // 고객용 뷰
        }

        // 직원인 경우
        if (staffType != null) {
            if (staffType.equals("chef") || staffType.equals("delivery")) {
                model.addAttribute("order", order);
                model.addAttribute("orderItem", orderItem);
                return "staff/order-detail-staff"; // 직원용 뷰
            }
        }

        // 세션없음
        return "redirect:/";
    }

    // TODO: 배달완료 시 deliveryTime set
}
