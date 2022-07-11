package com.mauriciotogneri.idlebattle;

import com.google.gson.Gson;

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
    private final Gson gson;

    public Server(int port, Engine engine)
    {
        super(new InetSocketAddress(port));

        this.engine = engine;
        this.gson = new Gson();
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull ClientHandshake clientHandshake)
    {
        System.out.println(webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, String message)
    {
        engine.onMessage(webSocket, gson.fromJson(message, Message.class));
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteBuffer message)
    {
        onMessage(webSocket, new String(message.array(), StandardCharsets.UTF_8));
    }

    @Override
    public void onClose(@NotNull WebSocket webSocket, int code, String reason, boolean remote)
    {
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
}