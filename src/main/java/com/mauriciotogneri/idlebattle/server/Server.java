package com.mauriciotogneri.idlebattle.server;

import com.mauriciotogneri.idlebattle.game.Engine;
import com.mauriciotogneri.idlebattle.game.Message;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Server extends WebSocketServer
{
    private final Engine engine;

    public Server(int port, Engine engine)
    {
        super(new InetSocketAddress(port));

        this.engine = engine;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull ClientHandshake clientHandshake)
    {
        System.out.println(webSocket.hashCode() + " [CONNECTED]");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, String message)
    {
        System.out.println(webSocket.hashCode() + " [RECEIVED] " + message);
        engine.onMessage(webSocket, Json.message(message));
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteBuffer message)
    {
        onMessage(webSocket, new String(message.array(), StandardCharsets.UTF_8));
    }

    @Override
    public void onClose(@NotNull WebSocket webSocket, int code, String reason, boolean remote)
    {
        System.out.println(webSocket.hashCode() + " [CLOSED]");
        engine.onClose(webSocket);
    }

    @Override
    public void onError(WebSocket webSocket, @NotNull Exception e)
    {
        e.printStackTrace();
    }

    @Override
    public void onStart()
    {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public static void send(@NotNull WebSocket webSocket, Message message)
    {
        String text = Json.string(message);
        webSocket.send(text);
        System.out.println(webSocket.hashCode() + " [SENT]     " + text);
    }
}