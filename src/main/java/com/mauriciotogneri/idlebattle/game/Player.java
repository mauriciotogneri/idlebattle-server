package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.app.Constants;
import com.mauriciotogneri.idlebattle.messages.OutputMessage;
import com.mauriciotogneri.idlebattle.messages.PlayerStatus;
import com.mauriciotogneri.idlebattle.server.Server;

import org.java_websocket.WebSocket;

public class Player
{
    private final WebSocket webSocket;
    private final int index;
    private final String name;

    private int points = 0;
    private double money = Constants.INITIAL_MONEY;
    private int mineLevel = 0;
    private int attackLevel = 0;

    public Player(WebSocket webSocket, int index, String name)
    {
        this.webSocket = webSocket;
        this.index = index;
        this.name = name;
    }

    public int index()
    {
        return index;
    }

    public String name()
    {
        return name;
    }

    public boolean has(WebSocket webSocket)
    {
        return (this.webSocket == webSocket);
    }

    public boolean increaseMine()
    {
        int cost = mineLevel * 100;

        if (money >= cost)
        {
            mineLevel++;
            money -= cost;

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean increaseAttack()
    {
        int cost = attackLevel * 100;

        if (money >= cost)
        {
            attackLevel++;
            money -= cost;

            return true;
        }
        else
        {
            return false;
        }
    }

    public void update(double dt)
    {
        money += mineLevel * Constants.MONEY_RATE * dt;
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
}
