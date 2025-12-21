package com.example.Nansy_desktop.service;

import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Service
public class WebSocketService {

    @Value("${websocket.server.url}")
    private String websocketUrl;

    @Value("${websocket.server.app}")
    private String appPrefix;

    @Value("${websocket.server.topic}")
    private String topicPrefix;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    public void connect() {
        try{
            WebSocketClient webSocketClient = new StandardWebSocketClient();

            stompClient = new WebSocketStompClient(webSocketClient);
            stompClient.setMessageConverter(new StringMessageConverter());

            StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    stompSession = session;

                    String echoTopic = topicPrefix + "/echo";
                    session.subscribe(echoTopic, new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return String.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            System.out.println(payload);
                        }
                    });
                }
            };
            stompClient.connectAsync(websocketUrl, sessionHandler);
            Thread.sleep(3000);
            sendTestMessage();
        } catch (Exception e) {
            System.out.println("ошибка подключения");
        }
    }

    public void sendMessage(String message) {
        if (stompSession != null && stompSession.isConnected()) {
            String destination = appPrefix + "/echo";
            stompSession.send(destination, message);
        } else {
            System.out.println("Нет подключения");
            reconnect();
        }
    }

    public void sendTestMessage() {
        sendMessage("Hello");
    }

    public void reconnect() {
        System.out.println("Reconnect...");
        try{ 
            Thread.sleep(3000);
            disconnect();
            connect();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void disconnect() {
        if (stompSession != null) {
            stompSession.disconnect();
            System.out.println("Disconnected");
        }
    }

    public boolean isConnected() {
        return stompSession != null && stompSession.isConnected();
    }
}