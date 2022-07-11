package com.mauriciotogneri.idlebattle;

import org.java_websocket.WebSocket;

public class WaitingPlayer
{
    public final WebSocket webSocket;
    public final String name;

    public WaitingPlayer(WebSocket webSocket, String name)
    {
        this.webSocket = webSocket;
        this.name = name;
    }
}
