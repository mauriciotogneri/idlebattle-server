package com.mauriciotogneri.idlebattle;

import org.java_websocket.WebSocket;
import org.jetbrains.annotations.NotNull;

public class Engine
{
    public void onMessage(@NotNull WebSocket webSocket, @NotNull Message message)
    {
        send(webSocket, message);
    }

    public void onClose(@NotNull WebSocket webSocket)
    {
    }

    public void send(@NotNull WebSocket webSocket, Message message)
    {
        String text = Json.string(message);
        webSocket.send(text);
        System.out.println(webSocket.hashCode() + " [SENT]     " + text);
    }
}
