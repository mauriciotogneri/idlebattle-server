package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.app.Constants;
import com.mauriciotogneri.idlebattle.messages.OutputMessage;
import com.mauriciotogneri.idlebattle.messages.PlayerIdentity;
import com.mauriciotogneri.idlebattle.messages.PlayerStatus;
import com.mauriciotogneri.idlebattle.server.Server;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.Nullable;

public class Player
{
    private final WebSocket webSocket;
    private final String name;
    private final int direction;

    private int points = 0;
    private double money = Constants.INITIAL_MONEY;
    private int mineLevel = 1;
    private int attackLevel = 1;

    public Player(WebSocket webSocket, String name, int direction)
    {
        this.webSocket = webSocket;
        this.name = name;
        this.direction = direction;
    }

    public int direction()
    {
        return direction;
    }

    public String name()
    {
        return name;
    }

    public boolean has(WebSocket webSocket)
    {
        return (this.webSocket == webSocket);
    }

    public boolean hasWon(int limit)
    {
        return points >= limit;
    }

    public void increaseMine()
    {
        int cost = mineLevel * 100;

        if (money >= cost)
        {
            mineLevel++;
            money -= cost;
        }
    }

    public void increaseAttack()
    {
        int cost = attackLevel * 100;

        if (money >= cost)
        {
            attackLevel++;
            money -= cost;
        }
    }

    @Nullable
    public Units buyUnits(int amount)
    {
        final int totalCost = Constants.UNIT_COST * amount;

        if (money >= totalCost)
        {
            money -= totalCost;

            return new Units(
                    direction,
                    amount,
                    Constants.UNIT_BASE_DAMAGE * attackLevel
            );
        }
        else
        {
            return null;
        }
    }

    public void update(double dt)
    {
        money += mineLevel * Constants.MONEY_RATE * dt;
    }

    public void addPoint()
    {
        points++;
    }

    public void send(OutputMessage message)
    {
        Server.send(webSocket, message);
    }

    public PlayerStatus status()
    {
        return new PlayerStatus(
                points,
                (int) money,
                mineLevel,
                attackLevel
        );
    }

    public PlayerIdentity identity(boolean isSelf)
    {
        return new PlayerIdentity(name, isSelf);
    }
}
