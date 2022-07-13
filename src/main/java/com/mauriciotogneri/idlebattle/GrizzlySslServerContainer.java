package com.mauriciotogneri.idlebattle;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.http.util.ContentType;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.strategies.WorkerThreadIOStrategy;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.tyrus.core.DebugContext;
import org.glassfish.tyrus.core.DebugContext.TracingThreshold;
import org.glassfish.tyrus.core.DebugContext.TracingType;
import org.glassfish.tyrus.core.TyrusWebSocketEngine;
import org.glassfish.tyrus.core.Utils;
import org.glassfish.tyrus.core.cluster.ClusterContext;
import org.glassfish.tyrus.core.monitoring.ApplicationEventListener;
import org.glassfish.tyrus.core.wsadl.model.Application;
import org.glassfish.tyrus.server.TyrusServerContainer;
import org.glassfish.tyrus.spi.ServerContainer;
import org.glassfish.tyrus.spi.ServerContainerFactory;
import org.glassfish.tyrus.spi.WebSocketEngine;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

public class GrizzlySslServerContainer extends ServerContainerFactory
{
    public static final String WORKER_THREAD_POOL_CONFIG = "org.glassfish.tyrus.container.grizzly.server.workerThreadPoolConfig";
    public static final String SELECTOR_THREAD_POOL_CONFIG = "org.glassfish.tyrus.container.grizzly.server.selectorThreadPoolConfig";

    public GrizzlySslServerContainer()
    {
    }

    public ServerContainer createContainer(Map<String, Object> properties)
    {
        final Object localProperties;
        if (properties == null)
        {
            localProperties = Collections.emptyMap();
        }
        else
        {
            localProperties = new HashMap(properties);
        }

        final Integer incomingBufferSize = (Integer) Utils.getProperty((Map) localProperties, "org.glassfish.tyrus.incomingBufferSize", Integer.class);
        final ClusterContext clusterContext = (ClusterContext) Utils.getProperty((Map) localProperties, "org.glassfish.tyrus.core.cluster.ClusterContext", ClusterContext.class);
        final ApplicationEventListener applicationEventListener = (ApplicationEventListener) Utils.getProperty((Map) localProperties, "org.glassfish.tyrus.core.monitoring.ApplicationEventListener", ApplicationEventListener.class);
        final Integer maxSessionsPerApp = (Integer) Utils.getProperty((Map) localProperties, "org.glassfish.tyrus.maxSessionsPerApp", Integer.class);
        final Integer maxSessionsPerRemoteAddr = (Integer) Utils.getProperty((Map) localProperties, "org.glassfish.tyrus.maxSessionsPerRemoteAddr", Integer.class);
        final Boolean parallelBroadcastEnabled = (Boolean) Utils.getProperty((Map) localProperties, "org.glassfish.tyrus.server.parallelBroadcastEnabled", Boolean.class);
        final DebugContext.TracingType tracingType = (DebugContext.TracingType) Utils.getProperty((Map) localProperties, "org.glassfish.tyrus.server.tracingType", DebugContext.TracingType.class, TracingType.OFF);
        final DebugContext.TracingThreshold tracingThreshold = (DebugContext.TracingThreshold) Utils.getProperty((Map) localProperties, "org.glassfish.tyrus.server.tracingThreshold", DebugContext.TracingThreshold.class, TracingThreshold.TRACE);
        return new TyrusServerContainer((Set) null)
        {
            private final WebSocketEngine engine = TyrusWebSocketEngine.builder(this).incomingBufferSize(incomingBufferSize).clusterContext(clusterContext).applicationEventListener(applicationEventListener).maxSessionsPerApp(maxSessionsPerApp).maxSessionsPerRemoteAddr(maxSessionsPerRemoteAddr).parallelBroadcastEnabled(parallelBroadcastEnabled).tracingType(tracingType).tracingThreshold(tracingThreshold).build();
            private HttpServer server;
            private String contextPath;
            private volatile NetworkListener listener = null;

            public void register(Class<?> endpointClass) throws DeploymentException
            {
                this.engine.register(endpointClass, this.contextPath);
            }

            public void register(ServerEndpointConfig serverEndpointConfig) throws DeploymentException
            {
                this.engine.register(serverEndpointConfig, this.contextPath);
            }

            public WebSocketEngine getWebSocketEngine()
            {
                return this.engine;
            }

            public void start(String rootPath, int port) throws IOException, DeploymentException
            {
                this.contextPath = rootPath;
                this.server = new HttpServer();
                ServerConfiguration config = this.server.getServerConfiguration();
                this.listener = new NetworkListener("grizzly", "0.0.0.0", port);
                this.server.addListener(this.listener);
                ThreadPoolConfig workerThreadPoolConfig = (ThreadPoolConfig) Utils.getProperty((Map) localProperties, "org.glassfish.tyrus.container.grizzly.server.workerThreadPoolConfig", ThreadPoolConfig.class);
                ThreadPoolConfig selectorThreadPoolConfig = (ThreadPoolConfig) Utils.getProperty((Map) localProperties, "org.glassfish.tyrus.container.grizzly.server.selectorThreadPoolConfig", ThreadPoolConfig.class);
                if (workerThreadPoolConfig == null && selectorThreadPoolConfig == null)
                {
                    this.server.getListener("grizzly").getTransport().setIOStrategy(WorkerThreadIOStrategy.getInstance());
                }
                else
                {
                    TCPNIOTransportBuilder transportBuilder = TCPNIOTransportBuilder.newInstance();
                    if (workerThreadPoolConfig != null)
                    {
                        transportBuilder.setWorkerThreadPoolConfig(workerThreadPoolConfig);
                    }

                    if (selectorThreadPoolConfig != null)
                    {
                        transportBuilder.setSelectorThreadPoolConfig(selectorThreadPoolConfig);
                    }

                    transportBuilder.setIOStrategy(WorkerThreadIOStrategy.getInstance());
                    this.server.getListener("grizzly").setTransport(transportBuilder.build());
                }

                this.server.getListener("grizzly").getKeepAlive().setIdleTimeoutInSeconds(-1);
                //this.server.getListener("grizzly").registerAddOn(new WebSocketAddOn(this, this.contextPath));
                WebSocketEngine webSocketEngine = this.getWebSocketEngine();
                Object staticContentPath = ((Map) localProperties).get("org.glassfish.tyrus.server.staticContentRoot");
                HttpHandler staticHandler = null;
                if (staticContentPath != null && !staticContentPath.toString().isEmpty())
                {
                    staticHandler = new StaticHttpHandler(new String[] {staticContentPath.toString()});
                }

                Object wsadl = ((Map) localProperties).get("org.glassfish.tyrus.server.wsadl");
                if (wsadl != null && wsadl.toString().equalsIgnoreCase("true"))
                {
                    config.addHttpHandler(new WsadlHttpHandler((TyrusWebSocketEngine) webSocketEngine, staticHandler));
                }
                else if (staticHandler != null)
                {
                    config.addHttpHandler(staticHandler);
                }

                if (applicationEventListener != null)
                {
                    applicationEventListener.onApplicationInitialized(rootPath);
                }

                this.server.start();
                super.start(rootPath, port);
            }

            public int getPort()
            {
                return this.listener != null && this.listener.getPort() > 0 ? this.listener.getPort() : -1;
            }

            public void stop()
            {
                super.stop();
                this.server.shutdownNow();
                if (applicationEventListener != null)
                {
                    applicationEventListener.onApplicationDestroyed();
                }

            }
        };
    }

    private static class WsadlHttpHandler extends HttpHandler
    {
        private final TyrusWebSocketEngine engine;
        private final HttpHandler staticHttpHandler;
        private JAXBContext wsadlJaxbContext;

        private WsadlHttpHandler(TyrusWebSocketEngine engine, HttpHandler staticHttpHandler)
        {
            this.engine = engine;
            this.staticHttpHandler = staticHttpHandler;
        }

        private synchronized JAXBContext getWsadlJaxbContext() throws JAXBException
        {
            if (this.wsadlJaxbContext == null)
            {
                this.wsadlJaxbContext = JAXBContext.newInstance(Application.class.getPackage().getName());
            }

            return this.wsadlJaxbContext;
        }

        public void service(Request request, Response response) throws Exception
        {
            if (request.getMethod().equals(Method.GET) && request.getRequestURI().endsWith("application.wsadl"))
            {
                this.getWsadlJaxbContext().createMarshaller().marshal(this.engine.getWsadlApplication(), response.getWriter());
                response.setStatus(200);
                response.setContentType(ContentType.newContentType("application/wsadl+xml"));
            }
            else
            {
                if (this.staticHttpHandler != null)
                {
                    this.staticHttpHandler.service(request, response);
                }
                else
                {
                    response.sendError(404);
                }

            }
        }
    }
}