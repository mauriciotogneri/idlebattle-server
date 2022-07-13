package com.mauriciotogneri.idlebattle.server;

import com.mauriciotogneri.idlebattle.game.Engine;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer
{
    @Override
    public void registerWebSocketHandlers(@NotNull WebSocketHandlerRegistry webSocketHandlerRegistry)
    {
        MessageHandler messageHandler = new MessageHandler(new Engine());
        new Thread(new Loop(messageHandler)).start();

        webSocketHandlerRegistry
                .addHandler(new Server(messageHandler), "/idlebattle")
                .setAllowedOriginPatterns("*");
    }
}