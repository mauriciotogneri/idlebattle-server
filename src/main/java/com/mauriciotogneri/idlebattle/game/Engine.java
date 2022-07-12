package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.app.Constants;
import com.mauriciotogneri.idlebattle.messages.MatchConfiguration;
import com.mauriciotogneri.idlebattle.messages.OutputMessage;
import com.mauriciotogneri.idlebattle.server.Logger;
import com.mauriciotogneri.idlebattle.server.Server;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Engine
{
    private final List<WaitingPublicPlayer> waitingPublic = new ArrayList<>();
    private final List<WaitingPrivatePlayer> waitingPrivate = new ArrayList<>();
    private final Map<String, Match> matches = new HashMap<>();

    public void joinPublic(WebSocket webSocket, String playerName)
    {
        if (waitingPublic.isEmpty())
        {
            waitingPublic.add(new WaitingPublicPlayer(webSocket, playerName));
            Server.send(webSocket, OutputMessage.waitingPublic());
        }
        else
        {
            WaitingPublicPlayer waitingPlayer = waitingPublic.remove(0);

            String matchId = Match.newId();
            Player player1 = new Player(waitingPlayer.webSocket, waitingPlayer.name, Constants.DIRECTION_UP);
            Player player2 = new Player(webSocket, playerName, Constants.DIRECTION_DOWN);

            startMatch(matchId, player1, player2);
        }
    }

    public void createPrivate(WebSocket webSocket, String playerName)
    {
        String matchId = Match.newId();

        waitingPrivate.add(new WaitingPrivatePlayer(
                webSocket,
                playerName,
                matchId
        ));

        Server.send(webSocket, OutputMessage.waitingPrivate(matchId));
    }

    public void joinPrivate(WebSocket webSocket, String matchId, String playerName)
    {
        WaitingPrivatePlayer waitingPlayer = getWaitingPrivate(matchId);

        if (waitingPlayer != null)
        {
            waitingPrivate.remove(waitingPlayer);

            Player player1 = new Player(waitingPlayer.webSocket, waitingPlayer.name, Constants.DIRECTION_UP);
            Player player2 = new Player(webSocket, playerName, Constants.DIRECTION_DOWN);

            startMatch(matchId, player1, player2);
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
    }

    public void increaseMine(WebSocket webSocket, String matchId)
    {
        Match match = matches.get(matchId);

        if (match != null)
        {
            match.onPlayerIncreaseMine(webSocket);
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
    }

    public void increaseAttack(WebSocket webSocket, String matchId)
    {
        Match match = matches.get(matchId);

        if (match != null)
        {
            match.onPlayerIncreaseAttack(webSocket);
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
    }

    public void launchUnits(WebSocket webSocket, String matchId, int laneId, int amount)
    {
        Match match = matches.get(matchId);

        if (match != null)
        {
            match.onPlayerLaunchUnits(webSocket, laneId, amount);
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
    }

    public void echo(WebSocket webSocket, String matchId)
    {
        Match match = matches.get(matchId);

        if (match != null)
        {
            Server.send(webSocket, OutputMessage.echo(matchId));
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
    }

    @Nullable
    private WaitingPrivatePlayer getWaitingPrivate(String matchId)
    {
        for (WaitingPrivatePlayer waitingPlayer : waitingPrivate)
        {
            if (waitingPlayer.matchId.equals(matchId))
            {
                return waitingPlayer;
            }
        }

        return null;
    }

    @Nullable
    private WaitingPrivatePlayer getWaitingPrivate(WebSocket webSocket)
    {
        for (WaitingPrivatePlayer waitingPlayer : waitingPrivate)
        {
            if (waitingPlayer.webSocket == webSocket)
            {
                return waitingPlayer;
            }
        }

        return null;
    }

    @Nullable
    private Match getMatch(WebSocket webSocket)
    {
        for (Match match : matches.values())
        {
            if (match.hasConnection(webSocket))
            {
                return match;
            }
        }

        return null;
    }

    private void startMatch(String matchId, @NotNull Player player1, @NotNull Player player2)
    {
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Match match = new Match(matchId, players, MatchConfiguration.create());
        match.start();

        matches.put(matchId, match);
    }

    public void onClose(@NotNull WebSocket webSocket)
    {
        if (!waitingPublic.isEmpty() && waitingPublic.get(0).webSocket == webSocket)
        {
            WaitingPublicPlayer waitingPlayer = waitingPublic.remove(0);
            Logger.onDisconnected(webSocket, String.format("Waiting for public match: (%s)", waitingPlayer.name));

            return;
        }

        WaitingPrivatePlayer waitingPlayer = getWaitingPrivate(webSocket);

        if (waitingPlayer != null)
        {
            waitingPrivate.remove(waitingPlayer);
            Logger.onDisconnected(webSocket, String.format("Waiting for private match: (%s, %s)", waitingPlayer.name, waitingPlayer.matchId));

            return;
        }

        Match match = getMatch(webSocket);

        if (match != null)
        {
            Player player = match.onPlayerDisconnected(webSocket);

            if (!match.hasPlayers())
            {
                removeMatch(match);
            }

            if (player != null)
            {
                Logger.onDisconnected(webSocket, String.format("From match: (%s, %s, %s)", match.id(), player.name(), player.direction()));

                return;
            }
        }

        Logger.log(webSocket, "Connection not found in any entity");
    }

    public void update(double dt)
    {
        for (Match match : matches.values())
        {
            match.update(dt);

            if (match.isFinished())
            {
                removeMatch(match);
            }
        }
    }

    private void removeMatch(@NotNull Match match)
    {
        matches.remove(match.id());
    }
}
