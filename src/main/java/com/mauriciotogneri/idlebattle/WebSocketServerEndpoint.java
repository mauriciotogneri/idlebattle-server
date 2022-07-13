package com.mauriciotogneri.idlebattle;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/idlebattle")
public class WebSocketServerEndpoint
{
    @OnOpen
    public void onOpen(@NotNull Session session)
    {
        System.out.println("[SERVER]: Handshake successful!!!!! - Connected!!!!! - Session ID: " + session.getId());
    }

    @OnMessage
    public String onMessage(@NotNull String message, Session session)
    {
        System.out.println("<<< " + message);

        if (message.equalsIgnoreCase("terminate"))
        {
            try
            {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Successfully session closed....."));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return message;
    }

    @OnClose
    public void onClose(@NotNull Session session, CloseReason closeReason)
    {
        System.out.println("[SERVER]: Session " + session.getId() + " closed, because " + closeReason);
    }
}