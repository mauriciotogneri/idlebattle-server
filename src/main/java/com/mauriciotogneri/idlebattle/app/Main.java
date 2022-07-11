package com.mauriciotogneri.idlebattle.app;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
{
    public static void main(@NotNull String[] args) throws InterruptedException, IOException
    {
        IdleBattleServer server = new IdleBattleServer(Integer.parseInt(args[0]));
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
