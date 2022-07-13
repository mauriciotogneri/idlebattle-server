package com.mauriciotogneri.idlebattle;

import org.glassfish.tyrus.spi.ServerContainer;
import org.glassfish.tyrus.spi.ServerContainerFactory;

import java.util.Map;

public class ServerContainerFactoryImpl extends ServerContainerFactory
{
    @Override
    public ServerContainer createContainer(Map<String, Object> map)
    {
        return new GrizzlySslServerContainer().createContainer(map);
    }
}
