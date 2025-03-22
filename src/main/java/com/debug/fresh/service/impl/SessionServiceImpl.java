package com.debug.fresh.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.pojo.Session;
import com.debug.fresh.service.SessionService;
import com.debug.fresh.mapper.SessionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
    private Integer getNextLoginSequence(Long userId) {
        Integer maxSeq = sessionMapper.selectMaxLoginSequence(userId);
        return (maxSeq == null) ? 1 : maxSeq + 1;
    }
}




