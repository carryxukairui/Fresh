package com.debug.fresh.config;

import jakarta.websocket.Session;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class WebSocketService {
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();

    // 当用户连接 WebSocket 时，存储用户的会话
    public void addUserSession(String token, Session session) {
        userSessions.put(token, session);
    }

    // 发送消息给某个用户在线设备
    public void sendMessageToUser(String token, String message) {
        Session session = userSessions.get(token);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 当用户断开连接时，移除 WebSocket 会话
    public void removeUserSession(String token) {
        userSessions.remove(token);
    }
}
