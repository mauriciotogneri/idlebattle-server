package com.mauriciotogneri.idlebattle.game;

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

            case MATCH_STARTED:
                break;
        }
    }

    private void joinPublic(WebSocket webSocket, Message message)
    {
        if (waitingPublic.isEmpty())
        {
            waitingPublic.add(new WaitingPublicPlayer(webSocket, message.playerName));
            Server.send(webSocket, Message.waitingPublic());
        }
        else
        {
            WaitingPublicPlayer waitingPlayer = waitingPublic.remove(0);

            String matchId = Match.newId();
            Player player1 = new Player(waitingPlayer.webSocket, 0, waitingPlayer.name);
            Player player2 = new Player(webSocket, 1, message.playerName);

            startMatch(matchId, player1, player2);
        }
    }

    private void createPrivate(WebSocket webSocket, @NotNull Message message)
    {
        String matchId = Match.newId();

        waitingPrivate.add(new WaitingPrivatePlayer(
                webSocket,
                message.playerName,
                matchId
        ));

        Server.send(webSocket, Message.waitingPrivate(matchId));
    }

    private void joinPrivate(WebSocket webSocket, @NotNull Message message)
    {
        WaitingPrivatePlayer waitingPlayer = getWaitingPrivate(message.matchId);

        if (waitingPlayer != null)
        {
            waitingPrivate.remove(waitingPlayer);

            String matchId = message.matchId;
            Player player1 = new Player(waitingPlayer.webSocket, 0, waitingPlayer.name);
            Player player2 = new Player(webSocket, 1, message.playerName);

            startMatch(matchId, player1, player2);
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

    private void startMatch(String matchId, @NotNull Player player1, @NotNull Player player2)
    {
        List<Player> players = new ArrayList<>();
        players.add(new Player(player1.webSocket, 0, player1.name));
        players.add(new Player(player2.webSocket, 1, player2.name));

        Match match = new Match(matchId, players);
        match.start();

        matches.put(matchId, match);
    }

    public synchronized void onClose(@NotNull WebSocket webSocket)
    {
    }
}
