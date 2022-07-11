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
        Player player = byWebSocket(webSocket);

        if (player != null)
        {
            players.remove(player);
            // TODO: inform the other player that the enemy has disconnected
            // TODO: if no more players connected, destroy the match

            if (!hasPlayers())
            {
                state = MatchState.FINISHED;
            }
        }

        return player;
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

            System.out.println("GAME LOOP");

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
