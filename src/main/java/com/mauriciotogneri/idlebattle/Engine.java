package com.mauriciotogneri.idlebattle;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Engine
{
    private final List<WaitingPlayer> waitingPublic = new ArrayList<>();
    private final Map<String, Match> matches = new HashMap<>();

    public synchronized void onMessage(@NotNull WebSocket webSocket, @NotNull Message message)
    {
        switch (message.event)
        {
            case JOIN_PUBLIC:
                joinPublic(webSocket, message);
                break;

            case CREATE_PRIVATE:
                break;

            case JOIN_PRIVATE:
                break;

            case MATCH_STARTED:
                break;
        }
    }

    private void joinPublic(WebSocket webSocket, Message message)
    {
        if (waitingPublic.isEmpty())
        {
            waitingPublic.add(new WaitingPlayer(webSocket, message.playerName));
            Server.send(webSocket, Message.waiting());
        }
        else
        {
            WaitingPlayer waitingPlayer = waitingPublic.remove(0);

            List<Player> players = new ArrayList<>();
            players.add(new Player(waitingPlayer.webSocket, 0, waitingPlayer.name));
            players.add(new Player(webSocket, 1, message.playerName));

            String matchId = UUID.randomUUID().toString();

            Match match = new Match(matchId, players);
            match.start();

            matches.put(matchId, match);
        }
    }

    public synchronized void onClose(@NotNull WebSocket webSocket)
    {
    }
}
