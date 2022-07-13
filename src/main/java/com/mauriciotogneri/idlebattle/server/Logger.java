package com.mauriciotogneri.idlebattle.server;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger
{
    public static void log(String message)
    {
        System.out.println(message);
    }

    public static void log(WebSocket webSocket, String message)
    {
        log(String.format("%s %s", (webSocket != null) ? webSocket.hashCode() : 0, message));
    }

    public static void onConnected(@NotNull WebSocket webSocket)
    {
        log(webSocket, "[CONNECTED]");
    }

    public static void onDisconnected(@NotNull WebSocket webSocket, String message)
    {
        log(webSocket, String.format("[DISCONNECTED] %s", message));
    }

    public static void onMessageReceived(@NotNull WebSocket webSocket, String message)
    {
        log(webSocket, String.format("[RECEIVED] %s", message));
    }

    public static void onMessageSent(@NotNull WebSocket webSocket, String message)
    {
        log(webSocket, String.format("[SENT] %s", message));
    }

    public static void onClosed(@NotNull WebSocket webSocket)
    {
        log(webSocket, "[CLOSED]");
    }

    public static void onError(WebSocket webSocket, @NotNull Exception e)
    {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String stackTrace = stringWriter.toString();

        log(webSocket, String.format("[ERROR] %s | %s", e.getLocalizedMessage(), stackTrace));
    }
}
