package com.mauriciotogneri.idlebattle;

import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.http.server.AddOn;
import org.glassfish.grizzly.http.server.HttpServerFilter;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.tyrus.spi.ServerContainer;

public class WebSocketAddOn implements AddOn
{
    private final ServerContainer serverContainer;
    private final String contextPath;

    public WebSocketAddOn(ServerContainer serverContainer, String contextPath)
    {
        this.serverContainer = serverContainer;
        this.contextPath = contextPath;
    }

    public void setup(NetworkListener networkListener, FilterChainBuilder builder)
    {
        int httpServerFilterIdx = builder.indexOfType(HttpServerFilter.class);
        if (httpServerFilterIdx >= 0)
        {
            builder.add(httpServerFilterIdx, new GrizzlyServerFilter(this.serverContainer, this.contextPath));
        }

    }
}