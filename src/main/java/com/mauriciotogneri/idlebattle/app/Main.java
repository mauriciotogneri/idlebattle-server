package com.mauriciotogneri.idlebattle.app;

import com.mauriciotogneri.idlebattle.game.Engine;
import com.mauriciotogneri.idlebattle.server.Logger;
import com.mauriciotogneri.idlebattle.server.MessageHandler;
import com.mauriciotogneri.idlebattle.server.Server;
import com.mauriciotogneri.idlebattle.ssl.SslConfig;

import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.jetbrains.annotations.NotNull;

public class Main
{
    public static void main(@NotNull String @NotNull [] args) throws Exception
    {
        MessageHandler messageHandler = new MessageHandler(new Engine());
        Server server = new Server(Integer.parseInt(args[0]), messageHandler);

        if ((args.length > 2) && args[1].equals("ssl"))
        {
            configureSSL(server, args[2]);
        }

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

    private static void configureSSL(@NotNull Server server, @NotNull String type) throws Exception
    {
        switch (type)
        {
            case "custom":
                server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(SslConfig.customContext()));
                break;

            case "apache":
                server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(SslConfig.apacheContext()));
                break;

            case "letsencrypt":
                server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(SslConfig.letsEncryptContext()));
                break;
        }

        Logger.log("Configured SSL with: " + type);
    }
}
