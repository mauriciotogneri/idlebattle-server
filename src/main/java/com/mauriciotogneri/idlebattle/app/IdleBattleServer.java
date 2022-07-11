package com.mauriciotogneri.idlebattle.app;

import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class IdleBattleServer extends WebSocketServer
{
    public IdleBattleServer(int port)
    {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(@NotNull org.java_websocket.WebSocket webSocket, @NotNull ClientHandshake clientHandshake)
    {
        webSocket.send("Welcome to the server!");
        System.out.println(webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onMessage(org.java_websocket.WebSocket conn, String message)
    {
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onMessage(org.java_websocket.WebSocket conn, @NotNull ByteBuffer message)
    {
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onClose(org.java_websocket.WebSocket conn, int code, String reason, boolean remote)
    {
        broadcast(conn + " has left the room!");
        System.out.println(conn + " has left the room!");
    }

    @Override
    public void onError(org.java_websocket.WebSocket conn, @NotNull Exception e)
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

    public static void main(String[] args) throws InterruptedException, IOException
    {
        IdleBattleServer server = new IdleBattleServer(8888);
        server.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true)
        {
            String in = reader.readLine();
            server.broadcast(in);

            if (in.equals("exit"))
            {
                server.stop(1000);
                break;
            }
        }
    }
}