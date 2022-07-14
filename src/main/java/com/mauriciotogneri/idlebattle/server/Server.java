package com.mauriciotogneri.idlebattle.server;

import com.mauriciotogneri.idlebattle.messages.OutputMessage;
import com.mauriciotogneri.idlebattle.utils.Json;
import com.mauriciotogneri.idlebattle.utils.Logger;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class Server extends TextWebSocketHandler
{
    private final Handler handler;

    public Server(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession webSocket) throws Exception
    {
        super.afterConnectionEstablished(webSocket);

        Logger.onConnected(webSocket);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession webSocket, @NotNull CloseStatus status) throws Exception
    {
        super.afterConnectionClosed(webSocket, status);

        Logger.onClosed(webSocket);
        handler.onClose(webSocket);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession webSocket, @NotNull TextMessage message) throws Exception
    {
        super.handleTextMessage(webSocket, message);

        Logger.onMessageReceived(webSocket, message.getPayload());
        handler.onMessage(webSocket, Json.message(message.getPayload()));
    }

    public static void send(@NotNull WebSocketSession webSocket, OutputMessage message)
    {
        try
        {
            String text = Json.string(message);
            webSocket.sendMessage(new TextMessage(text));
            Logger.onMessageSent(webSocket, text);
        }
        catch (Exception e)
        {
            Logger.onError(webSocket, e);
        }
    }
}