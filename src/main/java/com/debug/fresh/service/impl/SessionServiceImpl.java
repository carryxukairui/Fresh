package com.debug.fresh.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.contants.RedisConstants;
import com.debug.fresh.pojo.Session;
import com.debug.fresh.service.SessionService;
import com.debug.fresh.mapper.SessionMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.debug.fresh.contants.RedisConstants.USER_KICKOUT_PREFIX;
import static com.debug.fresh.contants.RedisConstants.USER_SESSIONS_PREFIX;


/**
* @author karry
* @description 针对表【session(登录会话表)】的数据库操作Service实现
* @createDate 2025-03-20 22:13:38
*/
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session>
    implements SessionService{

    @Resource
    private SessionMapper sessionMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void createSession(Integer userId, String tokenValue, String deviceHash, String ipAddress, String clientInfo) {
        // 6. 创建会话记录
        Session session = new Session();
        session.setUserId(userId);
        session.setDeviceHash(deviceHash);
        session.setToken(StpUtil.getTokenValue());
        session.setClientInfo(clientInfo);
        sessionMapper.insert(session);

    }

    @Override
    public boolean logoutDevice(String deviceHash) {
        Integer userId = StpUtil.getLoginIdAsInt();
        Session l = sessionMapper.selectOne(new LambdaQueryWrapper<Session>()
                .eq(Session::getUserId, userId)
                .eq(Session::getDeviceHash, deviceHash)
                .eq(Session::getIsValid,1));
        if (l==null){
            return false;
        }
        String token = l.getToken();
        String sessionKey = USER_SESSIONS_PREFIX + userId;
        // 从 Redis 中移除会话记录
        stringRedisTemplate.opsForList().remove(sessionKey, 1, token);
        // 使 Sa-Token 的会话失效
        StpUtil.logoutByTokenValue(token);
        l.setIsValid(0);
        sessionMapper.updateById(l);
        return  true;
    }



}




