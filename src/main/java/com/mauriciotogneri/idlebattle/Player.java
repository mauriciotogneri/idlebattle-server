package com.mauriciotogneri.idlebattle;

import org.java_websocket.WebSocket;

public class Player
{
    public final WebSocket webSocket;
    public final int index;
    public final String name;

    public Player(WebSocket webSocket, int index, String name)
    {
        this.webSocket = webSocket;
        this.index = index;
        this.name = name;
    }
}
