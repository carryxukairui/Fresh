package com.debug.fresh.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.debug.fresh.contants.RedisConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.debug.fresh.contants.RedisConstants.*;


@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null) {
            Integer userId = (Integer) StpUtil.getLoginIdByToken(token);
            if (userId != null) {
                // 检查是否被踢出
                String kickoutKey = USER_KICKOUT_PREFIX + userId + ":" + token;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(kickoutKey))) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("您的账号已在其他设备登录，您已被迫下线。");
                    return false;
                }
                // 检查密码版本
                String currentPwdVersion = (String) StpUtil.getSession().get("password_version");
                String latestPwdVersion = redisTemplate.opsForValue().get(USER_PWD_VERSION_PREFIX + userId);
                if (latestPwdVersion != null && !latestPwdVersion.equals(currentPwdVersion)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("您的密码已被修改，请重新登录。");
                    return false;
                }
                // 检查昵称修改
                String newNickname = redisTemplate.opsForValue().get(USER_NICKNAME_PREFIX + userId);
                if (newNickname != null) {
                    response.setHeader("X-New-Nickname", newNickname);
                    redisTemplate.delete(RedisConstants.USER_NICKNAME_PREFIX + userId);
                }
            }
        }
        return true;
    }

}
