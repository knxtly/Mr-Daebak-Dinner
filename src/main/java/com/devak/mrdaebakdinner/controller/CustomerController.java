package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.CustomerDTO;
import com.devak.mrdaebakdinner.entity.CustomerEntity;
import com.devak.mrdaebakdinner.exception.CustomerNotFoundException;
import com.devak.mrdaebakdinner.exception.DatabaseException;
import com.devak.mrdaebakdinner.exception.DuplicateLoginIdException;
import com.devak.mrdaebakdinner.exception.IncorrectPasswordException;
import com.devak.mrdaebakdinner.service.CustomerService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // customer 기본화면 (로그인 화면)
    @GetMapping("/customer")
    public String showCustomerInterface() {
        return "customer/customer";
    }

    // 로그인 요청
    @PostMapping("/customer/login")
    public String login(@ModelAttribute CustomerDTO customerDTO,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {
        // ID, PW가 입력되지 않았을 때 errorMessage
        if (customerDTO.getLoginId() == null || customerDTO.getLoginId().isBlank() ||
                customerDTO.getPassword() == null || customerDTO.getPassword().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "올바른 ID와 PW를 입력해주세요");
            return "redirect:/customer";
        }

        try {
            CustomerDTO loginResult = customerService.login(customerDTO);
            // 로그인 성공: 세션에 사용자 정보 저장
            session.setAttribute("loggedInCustomer", loginResult);
            // 로그인 성공했을 때 고객의 이전주문내역을 바로 보여주라고 요청한다면,
            // return "redirect:/customer/orders/history"
            return "redirect:/customer/main";
        } catch (IncorrectPasswordException | CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
    public String signUp(@Valid @ModelAttribute CustomerDTO customerDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        // 유효성 검사(@Valid + BindingResult)
        if (bindingResult.hasErrors()) {
            // 오류 메시지 이어붙이기
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMessage.append(fieldError.getDefaultMessage()).append("<br>");
            });

            // redirectAttributes에 errorMessage 전달
            redirectAttributes.addFlashAttribute("errorMessage",
                    errorMessage.toString().trim());
            return "redirect:/customer/signup";
        }

        try { // 입력값이 Valid하다면,
            // 회원가입 시도
            customerService.signUp(customerDTO);
            return "redirect:/customer";
        } catch (DuplicateLoginIdException | DataAccessException e) {
            // 회원가입 실패하면 errorMessage 출력
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customer/signup";
        }
    }

    // 메인 페이지 GET 요청
    @GetMapping("/customer/main")
    public String showCustomerMain(HttpSession session, Model model) {
        // session에 로그인 정보 있는지 확인
        CustomerDTO loggedInCustomer = (CustomerDTO) session.getAttribute("loggedInCustomer");

        // 세션정보 없으면 로그인 페이지(/customer)로 GET요청
        if (loggedInCustomer == null) {
            return "redirect:/customer";
        }
        // 세션정보 있으면 Model에 담아서 활용
        model.addAttribute("loggedInCustomer", loggedInCustomer);
        return "customer/main";
    }

    // 로그아웃 요청
    @GetMapping("/customer/logout")
    public String customerLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/customer";
    }

}
