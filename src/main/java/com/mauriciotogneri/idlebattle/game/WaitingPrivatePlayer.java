package com.mauriciotogneri.idlebattle.game;

import org.java_websocket.WebSocket;

public class WaitingPrivatePlayer
{
    public final WebSocket webSocket;
    public final String name;
    public final String matchId;

    public WaitingPrivatePlayer(WebSocket webSocket, String name, String matchId)
    {
        this.webSocket = webSocket;
        this.name = name;
        this.matchId = matchId;
    }
}
