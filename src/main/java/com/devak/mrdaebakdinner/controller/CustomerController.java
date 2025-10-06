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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    /* ============ Login ============ */

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
                                Model model,
                                HttpSession session) {
        if (bindingResult.hasErrors()) {
            StringBuilder loginErrMsg = new StringBuilder();

            // 원하는 출력 순서
            List<String> fieldOrder = List.of("loginId", "password");

            // fieldOrder 순서대로 정렬. 없는 필드면 맨 뒤로
            List<FieldError> sortedErrors = bindingResult.getFieldErrors().stream()
                    .sorted(Comparator.comparingInt(
                            e -> !fieldOrder.contains(e.getField()) ? Integer.MAX_VALUE : fieldOrder.indexOf(e.getField())
                    ))
                    .toList();
            for (FieldError fieldError : sortedErrors) {
                loginErrMsg.append(fieldError.getDefaultMessage()).append("\n");
            }

            model.addAttribute("loginErrMsg", loginErrMsg.toString().trim());
            return "customer/customer";
        }

        try {
            // Staff 세션 있으면 삭제
            if (session.getAttribute("loggedInStaff") != null) {
                session.removeAttribute("loggedInStaff");
            }
            // 로그인 시도
            CustomerSessionDTO customerSessionDTO = customerService.login(customerLoginDTO);
            session.setAttribute("loggedInCustomer", customerSessionDTO);
            return "redirect:/customer/main";
        } catch (IncorrectPasswordException | CustomerNotFoundException e) { // 로그인 실패
            model.addAttribute("loginErrMsg", e.getMessage());
            return "customer/customer";
        }
    }

    /* ============ SignUp ============ */

    // 회원가입 페이지 GET 요청
    @GetMapping("/customer/signup")
    public String showSignUp(@ModelAttribute("customerSignUpDTO") CustomerSignUpDTO customerSignUpDTO) {
        // 빈 DTO를 폼에 담아 렌더링
        return "customer/signup";
    }

    // 회원가입 페이지 POST 요청
    @PostMapping("/customer/signup")
    public String signUp(@Valid @ModelAttribute("customerSignUpDTO") CustomerSignUpDTO customerSignUpDTO,
                         BindingResult bindingResult,
                         Model model) {
        // valid check 실패
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("loginId")) {
                model.addAttribute("signUpLoginIdErrMsg",
                        bindingResult.getFieldError("loginId").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("password")) {
                model.addAttribute("signUpPasswordErrMsg",
                        bindingResult.getFieldError("password").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("name")) {
                model.addAttribute("signUpNameErrMsg",
                        bindingResult.getFieldError("name").getDefaultMessage());
            }
            return "customer/signup";
        }

        try { // 입력값이 Valid하다면, 회원가입 시도
            customerService.signUp(customerSignUpDTO);
            return "redirect:/customer";
        } catch (DuplicateLoginIdException | DataAccessException e) {
            // 회원가입 실패하면 errMsg 출력
            model.addAttribute("signUpErrMsg", e.getMessage());
            return "customer/signup";
        }
    }

    /* ============ Main ============ */

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

    /* ============ Logout ============ */

    // 로그아웃 요청
    @GetMapping("/customer/logout")
    public String customerLogout(HttpSession session) {
        session.removeAttribute("loggedInCustomer");
        return "redirect:/";
    }

}
