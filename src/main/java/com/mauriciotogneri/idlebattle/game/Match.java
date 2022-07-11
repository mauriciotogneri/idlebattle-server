package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.messages.MatchStatus;
import com.mauriciotogneri.idlebattle.messages.OutputMessage;
import com.mauriciotogneri.idlebattle.server.Server;
import com.mauriciotogneri.idlebattle.types.MatchState;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Match
{
    private final String id;
    private final List<Player> players;
    private MatchState state = MatchState.CREATED;

    public Match(String id, List<Player> players)
    {
        this.id = id;
        this.players = players;
    }

    public String id()
    {
        return id;
    }

    public boolean hasPlayers()
    {
        return !players.isEmpty();
    }

    public void start()
    {
        for (Player player : players)
        {
            player.send(OutputMessage.matchStarted(status(), player.status()));
        }

        state = MatchState.RUNNING;
    }

    public boolean hasConnection(WebSocket webSocket)
    {
        Player player = byWebSocket(webSocket);

        return (player != null);
    }

    @Nullable
    public Player onPlayerDisconnected(WebSocket webSocket)
    {
        Player disconnectedPlayer = byWebSocket(webSocket);

        if (disconnectedPlayer != null)
        {
            players.remove(disconnectedPlayer);

            for (Player player : players)
            {
                player.send(OutputMessage.playerDisconnected(disconnectedPlayer.name(), id));
            }

            if (!hasPlayers())
            {
                state = MatchState.FINISHED;
            }
        }

        return disconnectedPlayer;
    }

    public void onPlayerIncreaseMine(WebSocket webSocket)
    {
        Player player = byWebSocket(webSocket);

        if (player != null)
        {
            player.increaseMine();
            player.send(OutputMessage.playerUpdate(player.status()));
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(id));
        }
    }

    public void onPlayerIncreaseAttack(WebSocket webSocket)
    {
        Player player = byWebSocket(webSocket);

        if (player != null)
        {
            player.increaseAttack();
            player.send(OutputMessage.playerUpdate(player.status()));
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(id));
        }
    }

    public void onPlayerLaunchUnits(WebSocket webSocket, int laneId, int amount)
    {
        Player player = byWebSocket(webSocket);

        if (player != null)
        {
            // TODO
            broadcast(OutputMessage.matchUpdate(status()));
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(id));
        }
    }

    @Nullable
    private Player byWebSocket(WebSocket webSocket)
    {
        for (Player player : players)
        {
            if (player.has(webSocket))
            {
                return player;
            }
        }

        return null;
    }

    private void broadcast(OutputMessage message)
    {
        for (Player player : players)
        {
            player.send(message);
        }
    }

    @NotNull
    private MatchStatus status()
    {
        return new MatchStatus(id);
    }

    public void update(double dt)
    {
        if (state == MatchState.RUNNING)
        {
            for (Player player : players)
            {
                player.update(dt);
            }
        }
    }

    public static String newId()
    {
        return UUID.randomUUID().toString();
    }
}
