package com.debug.fresh.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.config.WebSocketService;
import com.debug.fresh.pojo.Session;
import com.debug.fresh.service.SessionService;
import com.debug.fresh.mapper.SessionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;


/**
* @author 28611
* @description 针对表【session(登录会话表)】的数据库操作Service实现
* @createDate 2025-03-20 22:13:38
*/
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session>
    implements SessionService{

    @Resource
    private SessionMapper sessionMapper;
    @Resource
    private WebSocketService webSocketService;
    @Override
    public void createSession(Integer userId, String tokenValue, String deviceHash, String ipAddress, String clientInfo) {
        // 6. 创建会话记录
        Session session = new Session();
        session.setUserId(userId);
        session.setDeviceHash(deviceHash);
        session.setToken(StpUtil.getTokenValue());
        session.setLoginSequence(getNextLoginSequence(Long.valueOf(userId)));
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
        StpUtil.logoutByTokenValue(l.getToken());
        l.setIsValid(0);
        sessionMapper.updateById(l);
        return  true;
    }


    @Override
    public void invalidateUserSessions(Integer userId) {
        List<Session> sessionList = sessionMapper.selectList(new LambdaQueryWrapper<Session>()
                .eq(Session::getUserId, userId)
                .eq(Session::getIsValid, 1));
        for (Session session : sessionList) {
            String token = session.getToken();

            webSocketService.sendMessageToUser(token, "密码已修改，请重新登录");
            StpUtil.kickoutByTokenValue(token);

            session.setIsValid(0);
            sessionMapper.updateById(session);
        }

    }

    private Integer getNextLoginSequence(Long userId) {
        Integer maxSeq = sessionMapper.selectMaxLoginSequence(userId);
        return (maxSeq == null) ? 1 : maxSeq + 1;
    }
}




