package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.types.MatchState;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Match implements Runnable
{
    private final String id;
    private final List<Player> players;
    private MatchState state = MatchState.CREATED;

    private static final int GAME_LOOP_STEP = 1000;

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
            player.send(Message.matchStarted(id));
        }

        state = MatchState.RUNNING;

        new Thread(this).start();
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
                player.send(Message.playerDisconnected(disconnectedPlayer.name(), id));
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
        }
    }

    public void onPlayerIncreaseAttack(WebSocket webSocket)
    {
        Player player = byWebSocket(webSocket);

        if (player != null)
        {
        }
    }

    public void onPlayerLaunchUnits(WebSocket webSocket, Message message)
    {
        Player player = byWebSocket(webSocket);

        if (player != null)
        {
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

    private void broadcast(Message message)
    {
        for (Player player : players)
        {
            player.send(message);
        }
    }

    public static String newId()
    {
        return UUID.randomUUID().toString();
    }

    @Override
    public void run()
    {
        while (state == MatchState.RUNNING)
        {
            // TODO: run game logic

            broadcast(Message.matchUpdate(id));

            try
            {
                Thread.sleep(GAME_LOOP_STEP);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
