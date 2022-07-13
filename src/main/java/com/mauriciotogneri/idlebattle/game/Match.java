package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.messages.LaneStatus;
import com.mauriciotogneri.idlebattle.messages.MatchConfiguration;
import com.mauriciotogneri.idlebattle.messages.MatchStatus;
import com.mauriciotogneri.idlebattle.messages.OutputMessage;
import com.mauriciotogneri.idlebattle.messages.PlayerStatus;
import com.mauriciotogneri.idlebattle.server.Server;
import com.mauriciotogneri.idlebattle.types.FinishState;
import com.mauriciotogneri.idlebattle.types.MatchState;
import com.mauriciotogneri.idlebattle.utils.MatchTimes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

public class Match
{
    private final String id;
    private final List<Player> players;
    private final Lane[] lanes;
    private final MatchConfiguration configuration;
    private final MatchTimes matchTimes;
    private MatchState state = MatchState.READY;
    private double readyTime = 0;
    private double totalTime = 0;

    public Match(String id, List<Player> players, @NotNull MatchConfiguration configuration, MatchTimes matchTimes)
    {
        this.id = id;
        this.players = players;

        this.lanes = new Lane[configuration.lanes];

        for (int i = 0; i < configuration.lanes; i++)
        {
            this.lanes[i] = new Lane(configuration);
        }

        this.configuration = configuration;
        this.matchTimes = matchTimes;
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
            player.send(OutputMessage.matchReady(status(player), configuration));
        }
    }

    public boolean hasConnection(WebSocketSession webSocket)
    {
        Player player = byWebSocket(webSocket);

        return (player != null);
    }

    @Nullable
    public Player onPlayerDisconnected(WebSocketSession webSocket)
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
                finishMatch(false);
            }
        }

        return disconnectedPlayer;
    }

    public void onPlayerIncreaseMine(WebSocketSession webSocket)
    {
        Player player = byWebSocket(webSocket);

        if (player != null)
        {
            player.increaseMine(configuration.mineCostMultiplier);
            sendMatchUpdate();
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(id));
        }
    }

    public void onPlayerIncreaseAttack(WebSocketSession webSocket)
    {
        Player player = byWebSocket(webSocket);

        if (player != null)
        {
            player.increaseAttack(configuration.attackCostMultiplier);
            sendMatchUpdate();
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(id));
        }
    }

    public void onPlayerLaunchUnits(WebSocketSession webSocket, int laneId, int amount)
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
                        broadcast(OutputMessage.unitsLaunched(laneId, amount, player.direction(), player.attackLevel()));
                    }
                    else
                    {
                        sendMatchUpdate();
                        Server.send(webSocket, OutputMessage.invalidAmount(amount));
                    }
                }
                else
                {
                    sendMatchUpdate();
                    Server.send(webSocket, OutputMessage.invalidAmount(amount));
                }
            }
            else
            {
                sendMatchUpdate();
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
        if ((id >= 0) && (id < lanes.length))
        {
            return lanes[id];
        }
        else
        {
            return null;
        }
    }

    @Nullable
    private Player byWebSocket(WebSocketSession webSocket)
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
    private MatchStatus status(Player player)
    {
        return new MatchStatus(
                id,
                (int) (configuration.matchTimeout - totalTime),
                playerStatus(players, player),
                laneStatus(lanes)
        );
    }

    @NotNull
    private PlayerStatus @NotNull [] playerStatus(@NotNull List<Player> players, Player self)
    {
        PlayerStatus[] result = new PlayerStatus[players.size()];

        for (int i = 0; i < players.size(); i++)
        {
            Player player = players.get(i);
            result[i] = player.status(player == self);
        }

        return result;
    }

    @NotNull
    private LaneStatus @NotNull [] laneStatus(Lane @NotNull [] lanes)
    {
        LaneStatus[] result = new LaneStatus[lanes.length];

        for (int i = 0; i < lanes.length; i++)
        {
            Lane lane = lanes[i];
            result[i] = lane.status();
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
            player.send(OutputMessage.matchUpdate(status(player)));
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

            finishMatch(true);
        }

        return (winner != null);
    }

    private void finishMatch(boolean writeTime)
    {
        state = MatchState.FINISHED;

        matchTimes.write((int)totalTime);
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
                finishMatch(true);
            }
            else if (player2Percentage > player1Percentage)
            {
                player2.send(OutputMessage.matchFinished(FinishState.WON));
                player1.send(OutputMessage.matchFinished(FinishState.LOST));
                finishMatch(true);
            }
            else
            {
                player1.send(OutputMessage.matchFinished(FinishState.TIE));
                player2.send(OutputMessage.matchFinished(FinishState.TIE));
                finishMatch(true);
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
