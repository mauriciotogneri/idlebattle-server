package com.mauriciotogneri.idlebattle.utils;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.WebSocketSession;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger
{
    public static void log(String message)
    {
        System.out.println(message);
    }

    public static void log(WebSocketSession webSocket, String message)
    {
        log(String.format("%s %s", (webSocket != null) ? webSocket.hashCode() : 0, message));
    }

    public static void onConnected(@NotNull WebSocketSession webSocket)
    {
        log(webSocket, "[CONNECTED]");
    }

    public static void onDisconnected(@NotNull WebSocketSession webSocket, String message)
    {
        log(webSocket, String.format("[DISCONNECTED] %s", message));
    }

    public static void onMessageReceived(@NotNull WebSocketSession webSocket, String message)
    {
        log(webSocket, String.format("[RECEIVED] %s", message));
    }

    public static void onMessageSent(@NotNull WebSocketSession webSocket, String message)
    {
        log(webSocket, String.format("[SENT] %s", message));
    }

    public static void onClosed(@NotNull WebSocketSession webSocket)
    {
        log(webSocket, "[CLOSED]");
    }

    public static void onError(WebSocketSession webSocket, @NotNull Exception e)
    {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String stackTrace = stringWriter.toString();

        log(webSocket, String.format("[ERROR] %s | %s", e.getLocalizedMessage(), stackTrace));
    }
}
