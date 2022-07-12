package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.messages.MatchConfiguration;
import com.mauriciotogneri.idlebattle.messages.OutputMessage;
import com.mauriciotogneri.idlebattle.messages.PlayerStatus;
import com.mauriciotogneri.idlebattle.server.Server;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Player
{
    private final WebSocket webSocket;
    private final String name;
    private final int direction;

    private int points = 0;
    private double money;
    private int mineLevel = 1;
    private int attackLevel = 1;

    public Player(WebSocket webSocket, String name, int direction, double money)
    {
        this.webSocket = webSocket;
        this.name = name;
        this.direction = direction;
        this.money = money;
    }

    public int direction()
    {
        return direction;
    }

    public int attackLevel()
    {
        return attackLevel;
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

    public void increaseMine(int multiplier)
    {
        int cost = mineLevel * multiplier;

        if (money >= cost)
        {
            mineLevel++;
            money -= cost;
        }
    }

    public void increaseAttack(int multiplier)
    {
        int cost = attackLevel * multiplier;

        if (money >= cost)
        {
            attackLevel++;
            money -= cost;
        }
    }

    @Nullable
    public Units buyUnits(@NotNull MatchConfiguration configuration, int amount)
    {
        final int totalCost = configuration.unitCost * amount;

        if (money >= totalCost)
        {
            money -= totalCost;

            return new Units(
                    configuration,
                    direction,
                    amount,
                    configuration.unitBaseDamage * attackLevel
            );
        }
        else
        {
            return null;
        }
    }

    public void update(int moneyRate, double dt)
    {
        money += mineLevel * moneyRate * dt;
    }

    public void addPoint()
    {
        points++;
    }

    public void addMoney(double value)
    {
        money += value;
    }

    public void send(OutputMessage message)
    {
        Server.send(webSocket, message);
    }

    public PlayerStatus status(boolean isSelf)
    {
        return new PlayerStatus(
                name,
                isSelf,
                direction,
                points,
                isSelf ? (int)money : null,
                isSelf ? mineLevel : null,
                isSelf ? attackLevel : null
        );
    }
}
