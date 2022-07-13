package com.mauriciotogneri.idlebattle;

import org.glassfish.tyrus.server.Server;

import java.util.Scanner;

public class WebSocketServer
{
    public static void main(String[] args)
    {
        Server server = null;

        try
        {
            server = new Server("localhost", 8888, "", null, WebSocketServerEndpoint.class);
            server.start();

            Scanner scanner = new Scanner(System.in);
            String inp = scanner.nextLine();
            scanner.close();

            if (inp.equalsIgnoreCase("t"))
            {
                System.out.println("[SERVER]: Server successfully terminated.....");
                server.stop();
            }
            else
            {
                System.out.println("[SERVER]: Invalid input!!!!!");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            server.stop();
        }
    }
}