package com.mauriciotogneri.idlebattle;

import com.mauriciotogneri.idlebattle.ssl.SslConfig;

import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.server.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WebSocketServer
{
    public static void main(String[] args)
    {
        Server server = null;

        try
        {
            SSLEngineConfigurator sslEngineConfigurator = new SSLEngineConfigurator(SslConfig.customContext()).setClientMode(false).setNeedClientAuth(false);

            NetworkListener listener = new NetworkListener("grizzly", "localhost", 8888);
            listener.setSecure(true);
            listener.setSSLEngineConfig(sslEngineConfigurator);

            Map<String, Object> properties = new HashMap<>();
            properties.put(ClientProperties.SSL_ENGINE_CONFIGURATOR, sslEngineConfigurator);

            server = new Server("localhost", 8888, "", properties, WebSocketServerEndpoint.class);
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