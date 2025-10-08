package com.devak.mrdaebakdinner.interceptor;

import com.devak.mrdaebakdinner.dto.CustomerSessionDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CustomerLoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 캐시 방지 헤더 추가
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxy 서버

        HttpSession session = request.getSession(false);
        CustomerSessionDTO customerSessionDTO = (session != null) ?
                (CustomerSessionDTO) session.getAttribute("loggedInCustomer") : null;
        if (customerSessionDTO == null){
            // 로그인 안 했으면 customer 로그인 페이지로 redirect
            response.sendRedirect(request.getContextPath() + "/customer");
            return false; // 요청 진행 막기
        }
        return true; // 요청 계속 진행
    }
}
