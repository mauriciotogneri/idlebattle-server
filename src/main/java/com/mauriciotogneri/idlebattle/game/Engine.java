package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.server.Server;
import com.mauriciotogneri.idlebattle.app.Constants;
import com.mauriciotogneri.idlebattle.messages.MatchConfiguration;
import com.mauriciotogneri.idlebattle.messages.OutputMessage;
import com.mauriciotogneri.idlebattle.server.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Engine
{
    private final List<WaitingPublicPlayer> waitingPublic = new ArrayList<>();
    private final List<WaitingPrivatePlayer> waitingPrivate = new ArrayList<>();
    private final Map<String, Match> matches = new HashMap<>();

    public void joinPublic(WebSocketSession webSocket, String playerName)
    {
        if (waitingPublic.isEmpty())
        {
            waitingPublic.add(new WaitingPublicPlayer(webSocket, playerName));
            Server.send(webSocket, OutputMessage.waitingPublic());
        }
        else
        {
            WaitingPublicPlayer waitingPlayer = waitingPublic.remove(0);

            MatchConfiguration configuration = MatchConfiguration.fromFile();
            String matchId = Match.newId();
            Player player1 = new Player(waitingPlayer.webSocket, waitingPlayer.name, Constants.DIRECTION_UP, configuration.initialMoney);
            Player player2 = new Player(webSocket, playerName, Constants.DIRECTION_DOWN, configuration.initialMoney);

            startMatch(matchId, configuration, player1, player2);
        }
    }

    public void createPrivate(WebSocketSession webSocket, String playerName)
    {
        String matchId = Match.newId();

        waitingPrivate.add(new WaitingPrivatePlayer(
                webSocket,
                playerName,
                matchId
        ));

        Server.send(webSocket, OutputMessage.waitingPrivate(matchId));
    }

    public void joinPrivate(WebSocketSession webSocket, String matchId, String playerName)
    {
        WaitingPrivatePlayer waitingPlayer = getWaitingPrivate(matchId);

        if (waitingPlayer != null)
        {
            waitingPrivate.remove(waitingPlayer);

            MatchConfiguration configuration = MatchConfiguration.fromFile();
            Player player1 = new Player(waitingPlayer.webSocket, waitingPlayer.name, Constants.DIRECTION_UP, configuration.initialMoney);
            Player player2 = new Player(webSocket, playerName, Constants.DIRECTION_DOWN, configuration.initialMoney);

            startMatch(matchId, configuration, player1, player2);
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
    }

    public void increaseMine(WebSocketSession webSocket, String matchId)
    {
        Match match = getMatch(webSocket, matchId);

        if (match != null)
        {
            match.onPlayerIncreaseMine(webSocket);
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
    }

    public void increaseAttack(WebSocketSession webSocket, String matchId)
    {
        Match match = getMatch(webSocket, matchId);

        if (match != null)
        {
            match.onPlayerIncreaseAttack(webSocket);
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
    }

    public void launchUnits(WebSocketSession webSocket, String matchId, int laneId, int amount)
    {
        Match match = getMatch(webSocket, matchId);

        if (match != null)
        {
            match.onPlayerLaunchUnits(webSocket, laneId, amount);
        }
        else
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
    }

    public void echo(WebSocketSession webSocket, String matchId)
    {
        Match match = getMatch(webSocket, matchId);

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
    private WaitingPrivatePlayer getWaitingPrivate(WebSocketSession webSocket)
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
    private Match getMatch(WebSocketSession webSocket)
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

    @Nullable
    private Match getMatch(WebSocketSession webSocket, String matchId)
    {
        Match match = matches.get(matchId);

        if ((match != null) && match.hasConnection(webSocket))
        {
            return match;
        }
        else
        {
            return null;
        }
    }

    private void startMatch(String matchId, MatchConfiguration configuration, @NotNull Player player1, @NotNull Player player2)
    {
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Match match = new Match(matchId, players, configuration);
        match.start();

        matches.put(matchId, match);
    }

    public void onClose(@NotNull WebSocketSession webSocket)
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
