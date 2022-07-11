package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.server.Server;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.NotNull;

public class MessageHandler
{
    private final Engine engine;

    public MessageHandler(Engine engine)
    {
        this.engine = engine;
    }

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
            engine.joinPublic(webSocket, playerName);
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
            engine.createPrivate(webSocket, playerName);
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
            engine.joinPrivate(webSocket, matchId, playerName);
        }
    }

    private void increaseMine(WebSocket webSocket, @NotNull Message message)
    {
        String matchId = message.matchId;

        if (valueNotEmpty(matchId))
        {
            engine.increaseMine(webSocket, matchId);
        }
    }

    private void increaseAttack(WebSocket webSocket, @NotNull Message message)
    {
        String matchId = message.matchId;

        if (valueNotEmpty(matchId))
        {
            engine.increaseAttack(webSocket, matchId);
        }
    }

    private void launchUnits(WebSocket webSocket, @NotNull Message message)
    {
        String matchId = message.matchId;
        Integer laneId = message.laneId;
        Integer amount = message.amount;

        if (valueNotEmpty(matchId) && valueNotEmpty(laneId) && valueNotEmpty(amount))
        {
            engine.launchUnits(webSocket, matchId, laneId, amount);
        }
    }

    public synchronized void onClose(@NotNull WebSocket webSocket)
    {
        engine.onClose(webSocket);
    }

    private boolean valueNotEmpty(String value)
    {
        return (value != null) && !value.trim().isEmpty();
    }

    private boolean valueNotEmpty(Integer value)
    {
        return (value != null);
    }
}
