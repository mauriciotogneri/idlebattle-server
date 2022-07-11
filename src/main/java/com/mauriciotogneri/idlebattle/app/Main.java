package com.mauriciotogneri.idlebattle.app;

import com.mauriciotogneri.idlebattle.game.Engine;
import com.mauriciotogneri.idlebattle.server.MessageHandler;
import com.mauriciotogneri.idlebattle.server.Server;

import org.jetbrains.annotations.NotNull;

public class Main
{
    public static void main(@NotNull String[] args)
    {
        MessageHandler messageHandler = new MessageHandler(new Engine());
        Server server = new Server(Integer.parseInt(args[0]), messageHandler);
        server.start();

        long lastTimestamp = System.nanoTime();

        while (true)
        {
            try
            {
                Thread.sleep(Constants.GAME_LOOP_STEP);

                long currentTimestamp = System.nanoTime();
                double dt = (currentTimestamp - lastTimestamp) / 1000000000d; // in seconds
                lastTimestamp = currentTimestamp;

                messageHandler.update(dt);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
