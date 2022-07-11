package com.mauriciotogneri.idlebattle;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.NotNull;

public class Engine
{
    public void onMessage(@NotNull WebSocket webSocket, @NotNull Message message)
    {
        System.out.println(webSocket.hashCode() + " [RECEIVED] " + message);
        webSocket.send(message.event);
        System.out.println(webSocket.hashCode() + " [SENT]     " + message.event);
    }

    public void onClose(@NotNull WebSocket webSocket)
    {
        System.out.println(webSocket.hashCode() + " [CLOSED]");
    }
}
