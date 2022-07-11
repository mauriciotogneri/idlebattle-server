package com.mauriciotogneri.idlebattle.server;

import com.mauriciotogneri.idlebattle.game.Message;
import com.mauriciotogneri.idlebattle.game.MessageHandler;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Server extends WebSocketServer
{
    private final MessageHandler handler;

    public Server(int port, MessageHandler handler)
    {
        super(new InetSocketAddress(port));

        this.handler = handler;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull ClientHandshake clientHandshake)
    {
        Logger.onConnected(webSocket);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, String message)
    {
        Logger.onMessageReceived(webSocket, message);
        handler.onMessage(webSocket, Json.message(message));
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteBuffer message)
    {
        onMessage(webSocket, new String(message.array(), StandardCharsets.UTF_8));
    }

    @Override
    public void onClose(@NotNull WebSocket webSocket, int code, String reason, boolean remote)
    {
        Logger.onClosed(webSocket);
        handler.onClose(webSocket);
    }

    @Override
    public void onError(WebSocket webSocket, @NotNull Exception e)
    {
        Logger.onError(webSocket, e);
    }

    @Override
    public void onStart()
    {
        Logger.log("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public static void send(@NotNull WebSocket webSocket, Message message)
    {
        String text = Json.string(message);
        webSocket.send(text);
        Logger.onMessageSent(webSocket, text);
    }
}