package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.messages.MatchConfiguration;
import com.mauriciotogneri.idlebattle.messages.MatchStatus;
import com.mauriciotogneri.idlebattle.messages.OutputMessage;
import com.mauriciotogneri.idlebattle.messages.PlayerIdentity;
import com.mauriciotogneri.idlebattle.server.Server;
import com.mauriciotogneri.idlebattle.types.FinishState;
import com.mauriciotogneri.idlebattle.types.MatchState;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Match
{
    private final String id;
    private final List<Player> players;
    private final List<Lane> lanes;
    private final MatchConfiguration configuration;
    private MatchState state = MatchState.CREATED;
    private double totalTime = 0;

    public Match(String id, List<Player> players, MatchConfiguration configuration)
    {
        this.id = id;
        this.players = players;

        this.lanes = new ArrayList<>();
        this.lanes.add(new Lane(0));
        this.lanes.add(new Lane(1));
        this.lanes.add(new Lane(2));
        this.lanes.add(new Lane(3));
        this.lanes.add(new Lane(4));

        this.configuration = configuration;
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
            player.send(OutputMessage.matchStarted(status(player), player.status()));
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
            Lane lane = getLane(laneId);

            if ((lane != null) && (lane.isEnabled()))
            {
                if (amount > 0)
                {
                    Units units = player.buyUnits(amount);
                    lane.launchUnits(units);
                    broadcast(OutputMessage.matchUpdate(status(player), player.status()));
                }
                else
                {
                    Server.send(webSocket, OutputMessage.invalidAmount(amount));
                }
            }
            else
            {
                Server.send(webSocket, OutputMessage.invalidLaneId(laneId));
            }
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(id));
        }
    }

    @Nullable
    private Lane getLane(int id)
    {
        if ((id >= 0) && (id < lanes.size()))
        {
            return lanes.get(id);
        }
        else
        {
            return null;
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
    private MatchStatus status(Player self)
    {
        return new MatchStatus(id, playerIdentities(players, self), lanes);
    }

    @NotNull
    private List<PlayerIdentity> playerIdentities(@NotNull List<Player> players, Player self)
    {
        List<PlayerIdentity> result = new ArrayList<>();

        for (Player player : players)
        {
            result.add(player.identity(player == self));
        }

        return result;
    }

    public boolean isFinished()
    {
        return (state == MatchState.FINISHED);
    }

    public void update(double dt)
    {
        if (state == MatchState.RUNNING)
        {
            totalTime += dt;

            for (Player player : players)
            {
                player.update(dt);
            }

            for (Lane lane : lanes)
            {
                lane.update(dt);
            }

            if (!checkWinnerByPoints())
            {
                if (totalTime >= configuration.timeout)
                {
                    checkWinnerByTerritory();
                }
            }
        }
    }

    private boolean checkWinnerByPoints()
    {
        Player winner = null;

        for (Player player : players)
        {
            if (player.hasWon(configuration.winnerLimit()))
            {
                winner = player;
            }
        }

        if (winner != null)
        {
            for (Player player : players)
            {
                if (player == winner)
                {
                    player.send(OutputMessage.matchFinished(FinishState.WON));
                }
                else
                {
                    player.send(OutputMessage.matchFinished(FinishState.LOST));
                }
            }

            state = MatchState.FINISHED;
        }

        return (winner != null);
    }

    private void checkWinnerByTerritory()
    {
        // TODO
    }

    public static String newId()
    {
        return UUID.randomUUID().toString();
    }
}
