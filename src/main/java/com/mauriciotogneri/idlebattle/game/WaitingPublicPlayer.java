package com.mauriciotogneri.idlebattle.game;

import org.springframework.web.socket.WebSocketSession;

public class WaitingPublicPlayer
{
    public final WebSocketSession webSocket;
    public final String name;

    public WaitingPublicPlayer(WebSocketSession webSocket, String name)
    {
        this.webSocket = webSocket;
        this.name = name;
    }
}
