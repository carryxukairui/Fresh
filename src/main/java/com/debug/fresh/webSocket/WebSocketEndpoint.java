package com.debug.fresh.webSocket;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@ServerEndpoint("/websocket/{token}")
@Component
@Slf4j
public class WebSocketEndpoint {
    private static WebSocketService webSocketService;

    @Autowired
    public void setWebSocketService(WebSocketService webSocketService) {
        WebSocketEndpoint.webSocketService = webSocketService;
    }

    @OnOpen
    public void onOpen(@PathParam("token") String token,
                       Session session) {
        String loginId = (String) StpUtil.getLoginIdByToken(token);
        Integer userId = Integer.parseInt(loginId);
        webSocketService.addUserSession(token, userId, session);
        // 启动心跳发送线程
        startHeartbeat(session);
        log.debug("连接websocket");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 可根据需要处理前端发送的消息
        // 获取前端发送的消息
        System.out.println("收到前端消息: " + message);

        // 处理消息的逻辑
        if ("hello".equals(message)) {
            try {
                session.getBasicRemote().sendText("你好，客户端！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("heartbeat".equals(message)) {
            // 心跳消息处理
            try {
                session.getBasicRemote().sendText("heartbeat");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                session.getBasicRemote().sendText("收到消息: " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose(@PathParam("token") String token) {
        webSocketService.removeUserSession(token);
    }
    /**
     * 启动心跳发送线程
     */
    private void startHeartbeat(Session session) {
        new Thread(() -> {
            while (session.isOpen()) {
                try {
                    // 发送心跳消息
                    session.getBasicRemote().sendText("heartbeat");
                    // 等待一段时间
                    Thread.sleep(30000); // 每30秒发送一次心跳
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }
}
