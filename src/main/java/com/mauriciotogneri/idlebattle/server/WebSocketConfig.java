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
        Handler handler = new Handler(new Engine());
        new Thread(new Loop(handler)).start();

        webSocketHandlerRegistry
                .addHandler(new Server(handler), "/idlebattle")
                .setAllowedOriginPatterns("*");
    }
}