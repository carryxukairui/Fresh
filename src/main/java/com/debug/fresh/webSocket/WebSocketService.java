package com.debug.fresh.webSocket;

import cn.hutool.json.JSONObject;
import jakarta.websocket.Session;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class WebSocketService {
    // 保存 token -> Session 映射
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();
    // 保存 token -> userId 映射
    private static final Map<String, Integer> tokenToUserMap = new ConcurrentHashMap<>();

    /**
     * 新连接时记录会话
     */
    public void addUserSession(String token, Integer userId, Session session) {
        userSessions.put(token, session);
        tokenToUserMap.put(token, userId);
    }

    /**
     * 向指定 token 发送 JSON 格式消息
     *
     * @param token   设备标识
     * @param type    消息类型（如 "kickOut", "passwordChange", "nicknameUpdate"）
     * @param content 消息内容
     */
    public void sendMessageToToken(String token, String type, String content) {
        Session session = userSessions.get(token);
        if (session != null && session.isOpen()) {
            try {
                JSONObject json = new JSONObject();
                json.put("type", type);
                json.put("content", content);
                session.getBasicRemote().sendText(json.toString());
            } catch (IOException e) {
                e.printStackTrace();
                // 这里可以记录日志或增加重试机制
            }
        }
    }

    /**
     * 向指定用户的所有在线设备发送消息
     */
    public void sendMessageToUser(Integer userId, String type, String content) {
        for (Map.Entry<String, Integer> entry : tokenToUserMap.entrySet()) {
            if (entry.getValue().equals(userId)) {
                sendMessageToToken(entry.getKey(), type, content);
            }
        }
    }

    /**
     * 当连接关闭时，移除对应会话
     */
    public void removeUserSession(String token) {
        userSessions.remove(token);
        tokenToUserMap.remove(token);
    }
}