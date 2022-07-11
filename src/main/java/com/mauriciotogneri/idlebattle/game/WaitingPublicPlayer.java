package com.mauriciotogneri.idlebattle.game;

import org.java_websocket.WebSocket;

public class WaitingPublicPlayer
{
    public final WebSocket webSocket;
    public final String name;

    public WaitingPublicPlayer(WebSocket webSocket, String name)
    {
        this.webSocket = webSocket;
        this.name = name;
    }
}
