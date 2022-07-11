package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.server.Server;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Match
{
    private final String id;
    private final List<Player> players;

    public Match(String id, List<Player> players)
    {
        this.id = id;
        this.players = players;
    }

    public String id()
    {
        return id;
    }

    public void start()
    {
        for (Player player : players)
        {
            Server.send(player.webSocket, Message.matchStarted(id));
        }
    }

    public boolean hasConnection(WebSocket webSocket)
    {
        Player player = byWebSocket(webSocket);

        return (player != null);
    }

    @Nullable
    public Player onPlayerDisconnected(WebSocket webSocket)
    {
        Player player = byWebSocket(webSocket);
        // TODO: inform the other player that the enemy has disconnected
        // TODO: if no more players connected, destroy the match

        return player;
    }

    @Nullable
    private Player byWebSocket(WebSocket webSocket)
    {
        for (Player player : players)
        {
            if (player.webSocket == webSocket)
            {
                return player;
            }
        }

        return null;
    }

    public static String newId()
    {
        return UUID.randomUUID().toString();
    }
}
