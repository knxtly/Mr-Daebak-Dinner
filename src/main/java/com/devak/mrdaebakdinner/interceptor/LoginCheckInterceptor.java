package com.devak.mrdaebakdinner.interceptor;

import com.devak.mrdaebakdinner.dto.CustomerLoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        CustomerLoginDTO customerDTO = (session != null) ?
                (CustomerLoginDTO) session.getAttribute("loggedInCustomer") : null;
        if (customerDTO == null){
            // 로그인 페이지로 redirect
            response.sendRedirect("/customer");
            return false; // 요청 진행 막기
        }
        return true; // 요청 계속 진행
    }
}
