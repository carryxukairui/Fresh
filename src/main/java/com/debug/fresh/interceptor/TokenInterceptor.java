package com.debug.fresh.interceptor;

import com.debug.fresh.util.JwtHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtHelper jwtHelper;  // 用于 Token 的解析和验证

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取 Token
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token is missing");
            return false;
        }

        // 校验 Token 是否有效
        if (jwtHelper.isExpiration(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token has expired");
            return false;
        }

        // 解析 Token，获取用户信息并放入请求上下文中
        String phone = jwtHelper.getPhone(token);
        if (phone == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid Token");
            return false;
        }

        // 将 phone 放到请求中，后续业务方法可以获取
        request.setAttribute("phone", phone);

        return true;  // 放行，继续执行后续的业务逻辑
    }
}
