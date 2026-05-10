package com.example.Nansy_desktop;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class StompWebSocketHandler {
    
    private WebSocket webSocket;
    private final List<Consumer<String>> messageListeners = new CopyOnWriteArrayList<>();
    private boolean isConnected = false;
    private String sessionId;
    private int subscriptionIdCounter = 0;
    private final Map<String, String> subscriptions = new HashMap<>();
    private final Map<String, Consumer<String>> subscriptionListeners = new HashMap<>();

    public void connect(String serverUrl, String username, String jwtToken) {
        try {
            String urlWithToken = serverUrl + "?token=" +jwtToken;

            HttpClient client = HttpClient.newHttpClient();
            
            webSocket = client.newWebSocketBuilder()
                .header("Authorization", "Bearer " + jwtToken)
                .buildAsync(URI.create(urlWithToken), new WebSocket.Listener() {

                    private final StringBuilder buffer = new StringBuilder();
                    
                    @Override
                    public void onOpen(WebSocket webSocket) {
                        System.out.println("WebSocket соединение установлено");
                        isConnected = true;
                        
                        String connectFrame = String.format(
                            "CONNECT\n" +
                            "accept-version:1.2\n" +
                            "host:localhost\n" +
                            "heart-beat:10000,10000\n" +
                            "\n" +
                            "\0"
                        );
                        
                        webSocket.sendText(connectFrame, true);
                        webSocket.request(1);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        buffer.append(data);
                        
                        if (last) {
                            String fullMessage = buffer.toString();
                            buffer.setLength(0);
                            handleStompFrame(fullMessage);
                            webSocket.request(1);
                        }
                        
                        return null;
                    }

                    @Override
                    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                        System.out.println("Соединение закрыто: " + reason + " (код: " + statusCode + ")");
                        isConnected = false;
                        return null;
                    }
                    
                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        System.err.println("WebSocket ошибка: " + error.getMessage());
                    }
                })
                .join();
                
        } catch (Exception e) {
            System.err.println("Не удалось подключиться: " + e.getMessage());
        }
    }

    private void handleStompFrame(String frame) {
        if (frame == null || frame.trim().isEmpty()) {
            System.err.println("получен пустой фрейм");
            return;
        }

        String[] lines = frame.split("\n");

        if (lines.length == 0) {
            System.err.println("фрейм не содержит строк");
            return;
        }

        String command = lines[0];

        if (command == null || command.isEmpty()) {
            System.err.println("команда в фрейме пустая");
            return;
        }

        switch (command) {
            case "CONNECTED":
                handleConnectedFrame(lines);
                break;
            case "MESSAGE":
                handleMessageFrame(lines);
                break;
            case "ERROR":
                handleErrorFrame(lines);
                break;
            default:
                System.out.println("Получена неизвестная команда: " + command);
        }
    }

    private void handleConnectedFrame(String[] lines) {
        for (String line : lines) {
            if (line.startsWith("session:")) {
                sessionId = line.substring("session:".length());
                System.out.println("STOMP подключен, session-id: " + sessionId);
                break;
            }
        }
    }

    private void handleMessageFrame(String[] lines) {
        Map<String, String> headers = new HashMap<>();
        StringBuilder body = new StringBuilder();
        boolean inBody = false;

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];

            if (line.isEmpty()) {
                inBody = true;
                continue;
            }

            if (!inBody) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String key = parts[0];
                    String value = parts[1].trim();
                    headers.put(key, value);
                }
            } else {
                if (line.endsWith("\0")) {
                    body.append(line.substring(0, line.length() - 1));
                } else {
                    if (i < lines.length - 1) {
                        body.append(line).append("\n");
                    } else {
                        body.append(line);
                    }
                }
            }
        }

        //String destination = headers.get("destination");
        String subscription = headers.get("subscription");
        String messageId = headers.get("message-id");
        String messageBody = body.toString();

        for (Consumer<String> listener : messageListeners){
            listener.accept(messageBody);
        }

        if (subscription != null && subscriptionListeners.containsKey(subscription)) {
            subscriptionListeners.get(subscription).accept(messageBody);
        }

        if (messageId != null) {
            sendAck(messageId);
        }
    }

    private void handleErrorFrame(String[] lines) {
        StringBuilder errorMessage = new StringBuilder("Stomp ошибка: ");

        for (int i = 1; i < lines.length; i++) {
            errorMessage.append(lines[i]);
            if (i < lines.length - 1) {
                errorMessage.append(" ");
            }
        }
        
        System.err.println(errorMessage.toString());
    }

    public String subscribe(String destination, Consumer<String> listener) {
        String subscriptionId = "sub-" + (++subscriptionIdCounter);

        String subscribeFrame = String.format(
            "SUBSCRIBE\n" +
            "id:%s\n" +
            "destination:%s\n" +
            "ack:client\n" +
            "\n" +
            "\0",
            subscriptionId, destination
        );

        webSocket.sendText(subscribeFrame, true);
        subscriptions.put(destination, subscriptionId);
        subscriptionListeners.put(subscriptionId, listener);

        return subscriptionId;
    }

    public void send(String destination, String message) {
    String sendFrame = String.format(
        "SEND\n" +
        "destination:%s\n" +
        "content-type:text/plain\n" +
        // "content-length:%d\n" +
        "\n" +
        "%s\n" +
        "\u0000",
        destination, message
    );
    
    System.out.println("📤 SEND to " + destination + ": " + message);
    webSocket.sendText(sendFrame, true);
    webSocket.request(1);
}

    public void unsubscribe(String subscriptionId) {
        String unsubscribeFrame = String.format(
            "UNSUBSCRIBE\n" +
            "id:%s\n" +
            "\n" +
            "\0",
            subscriptionId
        );
        
        webSocket.sendText(unsubscribeFrame, true);
        
        subscriptionListeners.remove(subscriptionId);
        
        String destinationToRemove = null;
        for (Map.Entry<String, String> entry : subscriptions.entrySet()) {
            if (entry.getValue().equals(subscriptionId)) {
                destinationToRemove = entry.getKey();
                break;
            }
        }
        if (destinationToRemove != null) {
            subscriptions.remove(destinationToRemove);
        }
    }

    public void disconnect() {
        if (webSocket != null && isConnected) {
            String disconnectFrame = String.format(
                "DISCONNECT\n" +
                "receipt:disconnect-ack\n" +
                "\n" +
                "\0"
            );
            
            webSocket.sendText(disconnectFrame, true);
            
            isConnected = false;
            
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "STOMP disconnect");
        }
    }

    private void sendAck(String messageId) {
        if (!isConnected) { 
            System.out.println("Ошибка отправки ACK: соединение разорвано"); 
            return;
        }

        try {
            String ackFrame = String.format(
                "ACK\n" +
                "id:\s\n" +
                "\n" +
                "\0",
                messageId
            );

            webSocket.sendText(ackFrame, true);

        } catch (Exception e) {
            System.out.println("Ошибка: " + e);
        }
    }

    public void addMessageListener(Consumer<String> listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(Consumer<String> listener) {
        messageListeners.remove(listener);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getSessionId() {
        return sessionId;
    }
}