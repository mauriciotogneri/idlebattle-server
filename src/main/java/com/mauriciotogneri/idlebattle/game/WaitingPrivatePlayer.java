package com.mauriciotogneri.idlebattle.game;

import org.springframework.web.socket.WebSocketSession;

public class WaitingPrivatePlayer
{
    public final WebSocketSession webSocket;
    public final String name;
    public final String matchId;

    public WaitingPrivatePlayer(WebSocketSession webSocket, String name, String matchId)
    {
        this.webSocket = webSocket;
        this.name = name;
        this.matchId = matchId;
    }
}
