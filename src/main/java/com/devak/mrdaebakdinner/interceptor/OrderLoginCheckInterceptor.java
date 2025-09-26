package com.devak.mrdaebakdinner.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class OrderLoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 캐시 방지 헤더
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        Object customer = session.getAttribute("loggedInCustomer");
        Object staff = session.getAttribute("loggedInStaff");

        if (customer == null && staff == null) {
            // 둘 다 없으면 로그인 필요
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        return true; // 고객이든 직원이든 있으면 통과
    }
}
