package com.debug.fresh.webSocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ServerEndpoint("/websocket/{userId}/{token}")
@Component
public class WebSocketEndpoint {
    private static WebSocketService webSocketService;

    @Autowired
    public void setWebSocketService(WebSocketService webSocketService) {
        WebSocketEndpoint.webSocketService = webSocketService;
    }

    @OnOpen
    public void onOpen(@PathParam("userId") Integer userId,
                       @PathParam("token") String token,
                       Session session) {
        webSocketService.addUserSession(token, userId, session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 可根据需要处理前端发送的消息
    }

    @OnClose
    public void onClose(@PathParam("token") String token) {
        webSocketService.removeUserSession(token);
    }
}
