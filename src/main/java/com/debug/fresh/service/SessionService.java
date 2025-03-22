package com.debug.fresh.service;

import com.debug.fresh.pojo.Session;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 28611
* @description 针对表【session(登录会话表)】的数据库操作Service
* @createDate 2025-03-20 22:13:38
*/
public interface SessionService extends IService<Session> {

    void createSession(Integer userId, String tokenValue, String deviceHash, String ipAddress, String clientInfo);
}
