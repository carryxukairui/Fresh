package com.debug.fresh.config;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ServerEndpoint("/websocket/{userId}")
@Component
public class WebSocketEndpoint {
    private static WebSocketService webSocketService;

    @Autowired
    public void setWebSocketService(WebSocketService webSocketService) {
        WebSocketEndpoint.webSocketService = webSocketService;
    }

    @OnOpen
    public void onOpen(@PathParam("token") String token, Session session) {
        webSocketService.addUserSession(token, session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 这里可以处理前端发送的消息（如果有的话）
    }

    @OnClose
    public void onClose(@PathParam("token") String token) {
        webSocketService.removeUserSession(token);
    }
}
