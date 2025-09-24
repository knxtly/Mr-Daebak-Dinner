package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.CustomerLoginDTO;
import com.devak.mrdaebakdinner.dto.CustomerSessionDTO;
import com.devak.mrdaebakdinner.dto.CustomerSignUpDTO;
import com.devak.mrdaebakdinner.dto.OrderHistoryDTO;
import com.devak.mrdaebakdinner.exception.CustomerNotFoundException;
import com.devak.mrdaebakdinner.exception.DuplicateLoginIdException;
import com.devak.mrdaebakdinner.exception.IncorrectPasswordException;
import com.devak.mrdaebakdinner.service.CustomerService;
import com.devak.mrdaebakdinner.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    // customer 기본화면 (로그인 화면)
    @GetMapping("/customer")
    public String showCustomerInterface(HttpSession session) {
        // 이미 customer session이 있으면 바로 main화면으로
        if (session.getAttribute("loggedInCustomer") != null) {
            return "redirect:/customer/main";
        }
        return "customer/customer";
    }

    // 로그인 요청
    @PostMapping("/customer/login")
    public String loginCustomer(@Valid @ModelAttribute CustomerLoginDTO customerLoginDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                HttpSession session,
                                HttpServletRequest request) {
        // 유효성 검사(@Valid + BindingResult): ID, PW가 입력되지 않았을 때 loginErrorMessage
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            if (bindingResult.getFieldError("loginId") != null) {
                errorMessage.append("ID");
            }
            if (bindingResult.getFieldError("password") != null) {
                if (!errorMessage.isEmpty()) {
                    errorMessage.append(", ");
                }
                errorMessage.append("PW");
            }
            errorMessage.append("는 필수 요소입니다.");
            redirectAttributes.addFlashAttribute("loginErrorMessage",
                    errorMessage.toString());
            return "redirect:/customer";
        }

        try {
            CustomerSessionDTO sessionDTO = customerService.login(customerLoginDTO);

            session.invalidate(); // 기존 세션 초기화
            HttpSession newSession = request.getSession(true); // 새 세션 발급

            newSession.setAttribute("loggedInCustomer", sessionDTO);
            return "redirect:/customer/main";
        } catch (IncorrectPasswordException | CustomerNotFoundException e) { // 로그인 실패
            redirectAttributes.addFlashAttribute("loginErrorMessage", e.getMessage());
            return "redirect:/customer";
        }
    }

    // 회원가입 페이지 GET 요청
    @GetMapping("/customer/signup")
    public String showSignUp() {
        return "customer/signup";
    }

    // 회원가입 페이지 POST 요청
    @PostMapping("/customer/signup")
    public String signUp(@Valid @ModelAttribute CustomerSignUpDTO customerDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // 오류 메시지 이어붙이기
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMessage.append(fieldError.getDefaultMessage()).append("<br>");
            });

            // redirectAttributes에 errorMessage 전달
            redirectAttributes.addFlashAttribute("signUpErrorMessage",
                    errorMessage.toString().trim());
            return "redirect:/customer/signup";
        }

        try { // 입력값이 Valid하다면,
            // 회원가입 시도
            customerService.signUp(customerDTO);
            return "redirect:/customer";
        } catch (DuplicateLoginIdException | DataAccessException e) {
            // 회원가입 실패하면 errorMessage 출력
            redirectAttributes.addFlashAttribute("signUpErrorMessage", e.getMessage());
            return "redirect:/customer/signup";
        }
    }

    // 메인 페이지 GET 요청
    @GetMapping("/customer/main")
    public String showCustomerMain(@SessionAttribute("loggedInCustomer") CustomerSessionDTO customerSessionDTO,
                                   HttpSession session,
                                   Model model) {
        // 최신 고객정보 조회
        CustomerSessionDTO freshCustomer = orderService.getFreshCustomerSessionDTO(customerSessionDTO.getLoginId());
        session.setAttribute("loggedInCustomer", freshCustomer); // 세션 갱신: 주문 후 VIP 반영

        model.addAttribute("loggedInCustomer", customerSessionDTO);

        // 고객의 loginId로 order목록을 찾아서 보여주는 로직
        List<OrderHistoryDTO> orderList =
                orderService.findOrderHistoryByLoginId(customerSessionDTO.getLoginId());
        // "orderList"라는 속성으로 전달
        model.addAttribute("orderList", orderList);
        return "customer/main";
    }

    // 로그아웃 요청
    @GetMapping("/customer/logout")
    public String customerLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/customer";
    }

}
