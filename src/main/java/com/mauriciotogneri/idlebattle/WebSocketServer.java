package com.mauriciotogneri.idlebattle;

import org.glassfish.tyrus.server.Server;

import java.util.HashMap;
import java.util.Scanner;

public class WebSocketServer
{
    public static void main(String[] args)
    {
        try
        {
            //NetworkListener listener = new NetworkListener("grizzly", "0.0.0.0", 8888);
            //listener.setSecure(true);
            //listener.setSSLEngineConfig(new SSLEngineConfigurator(getSslContextConfigurator()).setClientMode(false).setNeedClientAuth(false));

            Server server = new Server("localhost", 8888, "", new HashMap<>(), WebSocketServerEndpoint.class);
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
    }
}