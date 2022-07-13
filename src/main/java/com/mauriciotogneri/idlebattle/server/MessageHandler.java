package com.mauriciotogneri.idlebattle.server;

import com.mauriciotogneri.idlebattle.game.Engine;
import com.mauriciotogneri.idlebattle.messages.InputMessage;
import com.mauriciotogneri.idlebattle.messages.OutputMessage;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.WebSocketSession;

public class MessageHandler
{
    private final Engine engine;

    public MessageHandler(Engine engine)
    {
        this.engine = engine;
    }

    public synchronized void onMessage(@NotNull WebSocketSession webSocket, @NotNull InputMessage message)
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

            case DISCONNECT:
                disconnect(webSocket);
                break;

            case ECHO:
                echo(webSocket, message);
                break;
        }
    }

    private void joinPublic(WebSocketSession webSocket, @NotNull InputMessage message)
    {
        String playerName = message.playerName;

        if (!valueNotEmpty(playerName))
        {
            Server.send(webSocket, OutputMessage.invalidPlayerName(playerName));
        }
        else
        {
            engine.joinPublic(webSocket, playerName);
        }
    }

    private void createPrivate(WebSocketSession webSocket, @NotNull InputMessage message)
    {
        String playerName = message.playerName;

        if (!valueNotEmpty(playerName))
        {
            Server.send(webSocket, OutputMessage.invalidPlayerName(playerName));
        }
        else
        {
            engine.createPrivate(webSocket, playerName);
        }
    }

    private void joinPrivate(WebSocketSession webSocket, @NotNull InputMessage message)
    {
        String matchId = message.matchId;
        String playerName = message.playerName;

        if (!valueNotEmpty(matchId))
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
        else if (!valueNotEmpty(playerName))
        {
            Server.send(webSocket, OutputMessage.invalidPlayerName(playerName));
        }
        else
        {
            engine.joinPrivate(webSocket, matchId, playerName);
        }
    }

    private void increaseMine(WebSocketSession webSocket, @NotNull InputMessage message)
    {
        String matchId = message.matchId;

        if (!valueNotEmpty(matchId))
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
        else
        {
            engine.increaseMine(webSocket, matchId);
        }
    }

    private void increaseAttack(WebSocketSession webSocket, @NotNull InputMessage message)
    {
        String matchId = message.matchId;

        if (!valueNotEmpty(matchId))
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
        else
        {
            engine.increaseAttack(webSocket, matchId);
        }
    }

    private void launchUnits(WebSocketSession webSocket, @NotNull InputMessage message)
    {
        String matchId = message.matchId;
        Integer laneId = message.laneId;
        Integer amount = message.amount;

        if (!valueNotEmpty(matchId))
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
        else if (!valueNotEmpty(laneId))
        {
            Server.send(webSocket, OutputMessage.invalidLaneId(laneId));
        }
        else if (!valueNotEmpty(amount))
        {
            Server.send(webSocket, OutputMessage.invalidAmount(amount));
        }
        else
        {
            engine.launchUnits(webSocket, matchId, laneId, amount);
        }
    }

    private void disconnect(WebSocketSession webSocket)
    {
        engine.onClose(webSocket);
    }

    private void echo(WebSocketSession webSocket, @NotNull InputMessage message)
    {
        String matchId = message.matchId;

        if (!valueNotEmpty(matchId))
        {
            Server.send(webSocket, OutputMessage.invalidMatchId(matchId));
        }
        else
        {
            engine.echo(webSocket, matchId);
        }
    }

    public synchronized void onClose(@NotNull WebSocketSession webSocket)
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

    public synchronized void update(double dt)
    {
        engine.update(dt);
    }
}
