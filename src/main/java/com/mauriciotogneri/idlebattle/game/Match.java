package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.server.Server;

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

    public void start()
    {
        for (Player player : players)
        {
            Server.send(player.webSocket, Message.matchStarted(id));
        }
    }

    public static String newId()
    {
        return UUID.randomUUID().toString();
    }
}
