package com.mauriciotogneri.idlebattle.game;

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

    public synchronized void onMessage(@NotNull WebSocket webSocket, @NotNull Message message)
    {
        switch (message.event)
        {
            case JOIN_PUBLIC:
                joinPublic(webSocket, message);
                break;

            case CREATE_PRIVATE:
                createPrivate(webSocket, message);
                break;

            case JOIN_PRIVATE:
                joinPrivate(webSocket, message);
                break;

            case INCREASE_MINE:
                increaseMine(webSocket, message);
                break;

            case INCREASE_ATTACK:
                increaseAttack(webSocket, message);
                break;

            case LAUNCH_UNITS:
                launchUnits(webSocket, message);
                break;
        }
    }

    private void joinPublic(WebSocket webSocket, @NotNull Message message)
    {
        String playerName = message.playerName;

        if (valueNotEmpty(playerName))
        {
            if (waitingPublic.isEmpty())
            {
                waitingPublic.add(new WaitingPublicPlayer(webSocket, playerName));
                Server.send(webSocket, Message.waitingPublic());
            }
            else
            {
                WaitingPublicPlayer waitingPlayer = waitingPublic.remove(0);

                String matchId = Match.newId();
                Player player1 = new Player(waitingPlayer.webSocket, 0, waitingPlayer.name);
                Player player2 = new Player(webSocket, 1, playerName);

                startMatch(matchId, player1, player2);
            }
        }
        else
        {
            Server.send(webSocket, Message.invalidPlayerName(playerName));
        }
    }

    private void createPrivate(WebSocket webSocket, @NotNull Message message)
    {
        String playerName = message.playerName;

        if (valueNotEmpty(playerName))
        {
            String matchId = Match.newId();

            waitingPrivate.add(new WaitingPrivatePlayer(
                    webSocket,
                    playerName,
                    matchId
            ));

            Server.send(webSocket, Message.waitingPrivate(matchId));
        }
        else
        {
            Server.send(webSocket, Message.invalidPlayerName(playerName));
        }
    }

    private void joinPrivate(WebSocket webSocket, @NotNull Message message)
    {
        String matchId = message.matchId;
        String playerName = message.playerName;

        if (valueNotEmpty(matchId) && valueNotEmpty(playerName))
        {
            WaitingPrivatePlayer waitingPlayer = getWaitingPrivate(matchId);

            if (waitingPlayer != null)
            {
                waitingPrivate.remove(waitingPlayer);

                Player player1 = new Player(waitingPlayer.webSocket, 0, waitingPlayer.name);
                Player player2 = new Player(webSocket, 1, playerName);

                startMatch(matchId, player1, player2);
            }
            else
            {
                Server.send(webSocket, Message.invalidMatchId(matchId));
            }
        }
    }

    private void increaseMine(WebSocket webSocket, @NotNull Message message)
    {
        Match match = matches.get(message.matchId);

        if (match != null)
        {
            match.onPlayerIncreaseMine(webSocket);
        }
        else
        {
            Server.send(webSocket, Message.invalidMatchId(message.matchId));
        }
    }

    private void increaseAttack(WebSocket webSocket, @NotNull Message message)
    {
        Match match = matches.get(message.matchId);

        if (match != null)
        {
            match.onPlayerIncreaseAttack(webSocket);
        }
        else
        {
            Server.send(webSocket, Message.invalidMatchId(message.matchId));
        }
    }

    private void launchUnits(WebSocket webSocket, @NotNull Message message)
    {
        Match match = matches.get(message.matchId);

        if (match != null)
        {
            match.onPlayerLaunchUnits(webSocket, message);
        }
        else
        {
            Server.send(webSocket, Message.invalidMatchId(message.matchId));
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

        Match match = new Match(matchId, players);
        match.start();

        matches.put(matchId, match);
    }

    public synchronized void onClose(@NotNull WebSocket webSocket)
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

            if (player != null)
            {
                Logger.onDisconnected(webSocket, String.format("From match: (%s, %s, %s)", match.id(), player.name(), player.index()));
            }

            if (!match.hasPlayers())
            {
                matches.remove(match.id());
            }

            return;
        }

        Logger.log(webSocket, "Connection not found in any entity");
    }

    private boolean valueNotEmpty(String value)
    {
        return (value != null) && !value.trim().isEmpty();
    }
}
