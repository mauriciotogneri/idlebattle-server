package com.mauriciotogneri.idlebattle.utils;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.WebSocketSession;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger
{
    private static void log(WebSocketSession webSocket, String message)
    {
        System.out.printf("%s %s%n", (webSocket != null) ? webSocket.hashCode() : 0, message);
    }

    public static void onConnected(WebSocketSession webSocket)
    {
        log(webSocket, "[CONNECTED]");
    }

    public static void onDisconnected(WebSocketSession webSocket, String message)
    {
        log(webSocket, String.format("[DISCONNECTED] %s", message));
    }

    public static void onMessageReceived(WebSocketSession webSocket, String message)
    {
        log(webSocket, String.format("[RECEIVED] %s", message));
    }

    public static void onMessageSent(WebSocketSession webSocket, String message)
    {
        log(webSocket, String.format("[SENT] %s", message));
    }

    public static void onClosed(WebSocketSession webSocket)
    {
        log(webSocket, "[CLOSED]");
    }

    public static void onError(@NotNull Exception e)
    {
        System.err.printf("[ERROR] %s | %s%n", e.getLocalizedMessage(), stackTrace(e));
    }

    private static String stackTrace(@NotNull Exception e)
    {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));

        return stringWriter.toString();
    }
}
