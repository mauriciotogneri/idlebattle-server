package com.mauriciotogneri.idlebattle;

import java.util.List;

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
}
