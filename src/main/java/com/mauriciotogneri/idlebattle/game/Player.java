package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.server.Server;

import org.java_websocket.WebSocket;

public class Player
{
    private final WebSocket webSocket;
    private final int index;
    private final String name;
    private float money = 0;

    public Player(WebSocket webSocket, int index, String name)
    {
        this.webSocket = webSocket;
        this.index = index;
        this.name = name;
    }

    public int index()
    {
        return index;
    }

    public String name()
    {
        return name;
    }

    public boolean has(WebSocket webSocket)
    {
        return (this.webSocket == webSocket);
    }

    public void send(Message message)
    {
        Server.send(webSocket, message);
    }

    public Player player(int index)
    {
        return new Player(webSocket, index, name);
    }
}
