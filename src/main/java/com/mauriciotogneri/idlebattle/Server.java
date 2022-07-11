package com.mauriciotogneri.idlebattle;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Server extends WebSocketServer
{
    public Server(int port)
    {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull ClientHandshake clientHandshake)
    {
        webSocket.send("Welcome to the server!");
        System.out.println(webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, String message)
    {
        System.out.println(webSocket.hashCode() + ": " + message);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteBuffer message)
    {
        onMessage(webSocket, new String(message.array(), StandardCharsets.UTF_8));
    }

    @Override
    public void onClose(@NotNull WebSocket webSocket, int code, String reason, boolean remote)
    {
        System.out.println(webSocket.hashCode() + " has left the room!");
    }

    @Override
    public void onError(@NotNull WebSocket webSocket, @NotNull Exception e)
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