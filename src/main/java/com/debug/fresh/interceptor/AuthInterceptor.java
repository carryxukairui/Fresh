package com.debug.fresh.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.debug.fresh.contants.RedisConstants;
import com.debug.fresh.contants.ResultCode;
import com.debug.fresh.mapper.SessionMapper;
import com.debug.fresh.model.Result;
import com.debug.fresh.pojo.Session;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.debug.fresh.contants.RedisConstants.*;


@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;
@Resource
private SessionMapper sessionMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null) return sendResponse(response,Result.error("未传token"));

        try {
            String userId = (String) StpUtil.getLoginIdByToken(token);
            if (userId == null) return sendResponse(response,Result.noUseToken("token已失效"));

            // 1. 检查设备踢出状态
            String kickoutKey = RedisConstants.USER_KICKOUT_PREFIX + userId + ":" + token;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(kickoutKey))) {
                StpUtil.logoutByTokenValue(token);
                return sendResponse(response,
                        Result.kickOut( "您的账号已在其他设备登录"));
            }

            // 2. 检查密码版本
            String currentPwdVersion = (String) StpUtil.getSession().get("password_version");
            String latestPwdVersion = redisTemplate.opsForValue().get(RedisConstants.USER_PWD_VERSION_PREFIX + userId);
            if (latestPwdVersion != null && !latestPwdVersion.equals(currentPwdVersion)) {
                String sessionKey = USER_SESSIONS_PREFIX + userId;
                        kickDeviceSession(Integer.valueOf(userId), token);
                        redisTemplate.opsForList().remove(sessionKey, 1, token);
                        StpUtil.logoutByTokenValue(token);
                         return sendResponse(response,
                        Result.passwordChanged("您的密码已被修改"));

                }

            }
        catch (Exception e) {
            log.error("鉴权处理异常", e);
        }
        return true;
    }
    private boolean sendResponse(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        // 统一返回200，错误码在body中
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            String json = new ObjectMapper().writeValueAsString(result);
            response.getWriter().write(json);
        } catch (JsonProcessingException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return false;
    }
    public void kickDeviceSession(Integer userId, String targetToken) {
        Session session = sessionMapper.selectOne(new LambdaQueryWrapper<Session>()
                .eq(Session::getUserId, userId)
                .eq(Session::getIsValid, 1)
                .eq(Session::getToken, targetToken));
        if (session != null){
            session.setIsValid(0);
            sessionMapper.updateById(session);
        }
    }
}
