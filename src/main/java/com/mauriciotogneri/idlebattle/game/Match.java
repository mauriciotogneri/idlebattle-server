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
import java.util.stream.Collectors;

public class Match
{
    private final String id;
    private final List<Player> players;
    private final List<Lane> lanes;
    private final MatchConfiguration configuration;
    private MatchState state = MatchState.READY;
    private double readyTime = 0;
    private double totalTime = 0;

    public Match(String id, List<Player> players, @NotNull MatchConfiguration configuration)
    {
        this.id = id;
        this.players = players;

        this.lanes = new ArrayList<>();

        for (int i = 0; i < configuration.lanes; i++)
        {
            this.lanes.add(new Lane(configuration));
        }

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
            player.send(OutputMessage.matchReady(status(),
                                                 configuration,
                                                 playerIdentities(players, player),
                                                 player.status()));
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
            player.increaseMine(configuration.mineCostMultiplier);
            sendPlayerUpdate(player);
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
            player.increaseAttack(configuration.attackCostMultiplier);
            sendPlayerUpdate(player);
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
                    Units units = player.buyUnits(configuration, amount);

                    if (units != null)
                    {
                        lane.launchUnits(units);
                        broadcast(OutputMessage.unitsLaunched(laneId, amount, player.direction()));
                    }
                    else
                    {
                        sendPlayerUpdate(player);
                        Server.send(webSocket, OutputMessage.invalidAmount(amount));
                    }
                }
                else
                {
                    sendPlayerUpdate(player);
                    Server.send(webSocket, OutputMessage.invalidAmount(amount));
                }
            }
            else
            {
                sendPlayerUpdate(player);
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
    private MatchStatus status()
    {
        return new MatchStatus(
                id,
                (int)(configuration.matchTimeout - totalTime),
                lanes.stream().map(Lane::status).collect(Collectors.toList())
        );
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
        if (state == MatchState.READY)
        {
            readyTime += dt;

            if (readyTime >= configuration.readyTimeout)
            {
                state = MatchState.RUNNING;
                broadcast(OutputMessage.matchStarted());
            }
        }
        else if ((state == MatchState.RUNNING) && (players.size() == 2))
        {
            totalTime += dt;

            for (Player player : players)
            {
                player.update(configuration.moneyRate, dt);
            }

            boolean sendUpdate = false;

            for (Lane lane : lanes)
            {
                sendUpdate |= lane.update(dt, players.get(0), players.get(1));
            }

            if (sendUpdate)
            {
                sendMatchUpdate();
            }

            if (!checkWinnerByPoints())
            {
                if (totalTime >= configuration.matchTimeout)
                {
                    checkWinnerByTerritory();
                }
            }
        }
    }

    private void sendMatchUpdate()
    {
        for (Player player : players)
        {
            player.send(OutputMessage.matchUpdate(status(), player.status()));
        }
    }

    private void sendPlayerUpdate(@NotNull Player player)
    {
        player.send(OutputMessage.playerUpdate(player.status()));
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
        if (players.size() == 2)
        {
            Player player1 = players.get(0);
            Player player2 = players.get(1);

            int player1Percentage = player1Percentage();
            int player2Percentage = 100 - player1Percentage;

            if (player1Percentage > player2Percentage)
            {
                player1.send(OutputMessage.matchFinished(FinishState.WON));
                player2.send(OutputMessage.matchFinished(FinishState.LOST));
                state = MatchState.FINISHED;
            }
            else if (player2Percentage > player1Percentage)
            {
                player2.send(OutputMessage.matchFinished(FinishState.WON));
                player1.send(OutputMessage.matchFinished(FinishState.LOST));
                state = MatchState.FINISHED;
            }
            else
            {
                player1.send(OutputMessage.matchFinished(FinishState.TIE));
                player2.send(OutputMessage.matchFinished(FinishState.TIE));
                state = MatchState.FINISHED;
            }
        }
    }

    private int player1Percentage()
    {
        double sum = 0;

        for (Lane lane : lanes)
        {
            sum += lane.wall();
        }

        return (int) Math.round((sum / configuration.lanes) * 100);
    }

    public static String newId()
    {
        return UUID.randomUUID().toString();
    }
}
