package com.devak.mrdaebakdinner.interceptor;

import com.devak.mrdaebakdinner.dto.StaffSessionDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class StaffLoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 캐시 방지 헤더 추가
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        StaffSessionDTO staffSessionDTO = (session != null) ?
                (StaffSessionDTO) session.getAttribute("loggedInStaff") : null;
        if (staffSessionDTO == null){
            // 로그인 안 했으면 staff 로그인 페이지로 redirect
            response.sendRedirect(request.getContextPath() + "/staff");
            return false; // 요청 진행 막기
        }
        return true; // 요청 계속 진행
    }
}
