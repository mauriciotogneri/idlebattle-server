package com.mauriciotogneri.idlebattle;

import org.glassfish.tyrus.container.grizzly.server.GrizzlyServerContainer;
import org.glassfish.tyrus.spi.ServerContainer;
import org.glassfish.tyrus.spi.ServerContainerFactory;

import java.util.Map;

public class ServerContainerFactoryImpl extends ServerContainerFactory
{
    @Override
    public ServerContainer createContainer(Map<String, Object> map)
    {
        return new GrizzlyServerContainer().createContainer(map);

        //return new GrizzlySslServerContainer().createContainer(map);
    }
}
