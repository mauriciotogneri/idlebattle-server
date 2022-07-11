package com.mauriciotogneri.idlebattle.app;

import com.mauriciotogneri.idlebattle.game.Engine;
import com.mauriciotogneri.idlebattle.server.MessageHandler;
import com.mauriciotogneri.idlebattle.server.Server;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
{
    public static void main(@NotNull String[] args) throws InterruptedException, IOException
    {
        Server server = new Server(Integer.parseInt(args[0]), new MessageHandler(new Engine()));
        server.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true)
        {
            String in = reader.readLine();
            server.broadcast(in);

            if (in.equals("exit"))
            {
                server.stop(1000);
                break;
            }
        }
    }
}
