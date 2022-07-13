package com.mauriciotogneri.idlebattle;


import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.CloseListener;
import org.glassfish.grizzly.Closeable;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.ICloseType;
import org.glassfish.grizzly.attributes.Attribute;
import org.glassfish.grizzly.attributes.AttributeHolder;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.http.HttpContent;
import org.glassfish.grizzly.http.HttpHeader;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.HttpResponsePacket;
import org.glassfish.grizzly.http.Protocol;
import org.glassfish.grizzly.http.util.Parameters;
import org.glassfish.grizzly.memory.ByteBufferArray;
import org.glassfish.grizzly.utils.Charsets;
import org.glassfish.tyrus.container.grizzly.client.GrizzlyWriter;
import org.glassfish.tyrus.container.grizzly.client.TaskProcessor;
import org.glassfish.tyrus.core.CloseReasons;
import org.glassfish.tyrus.core.RequestContext;
import org.glassfish.tyrus.core.RequestContext.Builder;
import org.glassfish.tyrus.core.TyrusUpgradeResponse;
import org.glassfish.tyrus.core.Utils;
import org.glassfish.tyrus.spi.Connection;
import org.glassfish.tyrus.spi.ReadHandler;
import org.glassfish.tyrus.spi.ServerContainer;
import org.glassfish.tyrus.spi.UpgradeRequest;
import org.glassfish.tyrus.spi.UpgradeResponse;
import org.glassfish.tyrus.spi.WebSocketEngine;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.websocket.CloseReason;

class GrizzlyServerFilter extends BaseFilter {
    private static final Logger LOGGER = Grizzly.logger(GrizzlyServerFilter.class);
    private static final Attribute<Connection> TYRUS_CONNECTION;
    private static final Attribute<TaskProcessor> TASK_PROCESSOR;
    private final ServerContainer serverContainer;
    private final String contextPath;

    public GrizzlyServerFilter(ServerContainer serverContainer, String contextPath) {
        this.serverContainer = serverContainer;
        this.contextPath = contextPath.endsWith("/") ? contextPath : contextPath + "/";
    }

    public NextAction handleClose(FilterChainContext ctx) throws IOException {
        Connection connection = this.getConnection(ctx);
        if (connection != null) {
            TaskProcessor taskProcessor = this.getTaskProcessor(ctx);
            taskProcessor.processTask(new GrizzlyServerFilter.CloseTask(connection, CloseReasons.CLOSED_ABNORMALLY.getCloseReason(), ctx.getConnection()));
        }

        return ctx.getStopAction();
    }

    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        HttpContent message = (HttpContent)ctx.getMessage();
        Connection tyrusConnection = this.getConnection(ctx);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "handleRead websocket: {0} content-size={1} headers=\n{2}", new Object[]{tyrusConnection, message.getContent().remaining(), message.getHttpHeader()});
        }

        if (tyrusConnection == null) {
            HttpHeader header = message.getHttpHeader();
            if (!"websocket".equalsIgnoreCase(header.getUpgrade()) && message.getHttpHeader().isRequest()) {
                return ctx.getInvokeAction();
            } else {
                String ATTR_NAME = "org.glassfish.tyrus.container.grizzly.WebSocketFilter.HANDSHAKE_PROCESSED";
                AttributeHolder attributeHolder = ctx.getAttributes();
                if (attributeHolder != null) {
                    Object attribute = attributeHolder.getAttribute("org.glassfish.tyrus.container.grizzly.WebSocketFilter.HANDSHAKE_PROCESSED");
                    if (attribute != null) {
                        return ctx.getInvokeAction();
                    }

                    attributeHolder.setAttribute("org.glassfish.tyrus.container.grizzly.WebSocketFilter.HANDSHAKE_PROCESSED", true);
                }

                return this.handleHandshake(ctx, message);
            }
        } else {
            if (message.getContent().hasRemaining()) {
                Buffer buffer = message.getContent();
                message.recycle();
                ReadHandler readHandler = tyrusConnection.getReadHandler();
                TaskProcessor taskProcessor = this.getTaskProcessor(ctx);
                if (!buffer.isComposite()) {
                    taskProcessor.processTask(new GrizzlyServerFilter.ProcessTask(buffer.toByteBuffer(), readHandler));
                } else {
                    ByteBufferArray byteBufferArray = buffer.toByteBufferArray();
                    ByteBuffer[] array = (ByteBuffer[])byteBufferArray.getArray();

                    for(int i = 0; i < byteBufferArray.size(); ++i) {
                        taskProcessor.processTask(new GrizzlyServerFilter.ProcessTask(array[i], readHandler));
                    }

                    byteBufferArray.recycle();
                }
            }

            return ctx.getStopAction();
        }
    }

    private Connection getConnection(FilterChainContext ctx) {
        return (Connection)TYRUS_CONNECTION.get(ctx.getConnection());
    }

    private TaskProcessor getTaskProcessor(FilterChainContext ctx) {
        return (TaskProcessor)TASK_PROCESSOR.get(ctx.getConnection());
    }

    private NextAction handleHandshake(FilterChainContext ctx, HttpContent content) {
        UpgradeRequest upgradeRequest = createWebSocketRequest(content);
        if (!upgradeRequest.getRequestURI().getPath().startsWith(this.contextPath)) {
            return ctx.getInvokeAction();
        } else {
            UpgradeResponse upgradeResponse = new TyrusUpgradeResponse();
            WebSocketEngine.UpgradeInfo upgradeInfo = this.serverContainer.getWebSocketEngine().upgrade(upgradeRequest, upgradeResponse);
            switch (upgradeInfo.getStatus()) {
                case SUCCESS:
                    final org.glassfish.grizzly.Connection grizzlyConnection = ctx.getConnection();
                    this.write(ctx, upgradeResponse);
                    final Connection connection = upgradeInfo.createConnection(new GrizzlyWriter(ctx.getConnection()), new Connection.CloseListener() {
                        public void close(CloseReason reason) {
                            grizzlyConnection.close();
                        }
                    });
                    TYRUS_CONNECTION.set(grizzlyConnection, connection);
                    TASK_PROCESSOR.set(grizzlyConnection, new TaskProcessor());
                    grizzlyConnection.addCloseListener(new CloseListener() {
                        public void onClosed(Closeable closeable, ICloseType type) throws IOException {
                            connection.close(CloseReasons.GOING_AWAY.getCloseReason());
                            GrizzlyServerFilter.TYRUS_CONNECTION.remove(grizzlyConnection);
                            GrizzlyServerFilter.TASK_PROCESSOR.remove(grizzlyConnection);
                        }
                    });
                    return ctx.getStopAction();
                case HANDSHAKE_FAILED:
                    this.write(ctx, upgradeResponse);
                    content.recycle();
                    return ctx.getStopAction();
                case NOT_APPLICABLE:
                    this.writeTraceHeaders(ctx, upgradeResponse);
                    return ctx.getInvokeAction();
                default:
                    return ctx.getStopAction();
            }
        }
    }

    private void write(FilterChainContext ctx, UpgradeResponse response) {
        HttpResponsePacket responsePacket = ((HttpRequestPacket)((HttpContent)ctx.getMessage()).getHttpHeader()).getResponse();
        responsePacket.setProtocol(Protocol.HTTP_1_1);
        responsePacket.setStatus(response.getStatus());
        Iterator var4 = response.getHeaders().entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry)var4.next();
            responsePacket.setHeader((String)entry.getKey(), Utils.getHeaderFromList((List)entry.getValue()));
        }

        ctx.write(HttpContent.builder(responsePacket).build());
    }

    private void writeTraceHeaders(FilterChainContext ctx, UpgradeResponse upgradeResponse) {
        HttpResponsePacket responsePacket = ((HttpRequestPacket)((HttpContent)ctx.getMessage()).getHttpHeader()).getResponse();
        Iterator var4 = upgradeResponse.getHeaders().entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry)var4.next();
            if (((String)entry.getKey()).contains("X-Tyrus-Tracing-")) {
                responsePacket.setHeader((String)entry.getKey(), Utils.getHeaderFromList((List)entry.getValue()));
            }
        }

    }

    private static UpgradeRequest createWebSocketRequest(HttpContent requestContent) {
        HttpRequestPacket requestPacket = (HttpRequestPacket)requestContent.getHttpHeader();
        Parameters parameters = new Parameters();
        parameters.setQuery(requestPacket.getQueryStringDC());
        parameters.setQueryStringEncoding(Charsets.UTF8_CHARSET);
        Map<String, String[]> parameterMap = new HashMap();
        Iterator var4 = parameters.getParameterNames().iterator();

        while(var4.hasNext()) {
            String paramName = (String)var4.next();
            parameterMap.put(paramName, parameters.getParameterValues(paramName));
        }

        RequestContext requestContext = Builder.create().requestURI(URI.create(requestPacket.getRequestURI())).queryString(requestPacket.getQueryString()).parameterMap(parameterMap).secure(requestPacket.isSecure()).remoteAddr(requestPacket.getRemoteAddress()).build();
        Iterator var11 = requestPacket.getHeaders().names().iterator();

        while(var11.hasNext()) {
            String name = (String)var11.next();
            Iterator var7 = requestPacket.getHeaders().values(name).iterator();

            while(var7.hasNext()) {
                String headerValue = (String)var7.next();
                List<String> values = (List)requestContext.getHeaders().get(name);
                if (values == null) {
                    requestContext.getHeaders().put(name, Utils.parseHeaderValue(headerValue.trim()));
                } else {
                    values.addAll(Utils.parseHeaderValue(headerValue.trim()));
                }
            }
        }

        return requestContext;
    }

    static {
        TYRUS_CONNECTION = Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute(GrizzlyServerFilter.class.getName() + ".Connection");
        TASK_PROCESSOR = Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute(TaskProcessor.class.getName() + ".TaskProcessor");
    }

    private class CloseTask extends TaskProcessor.Task {
        private final Connection connection;
        private final CloseReason closeReason;
        private final org.glassfish.grizzly.Connection grizzlyConnection;

        private CloseTask(Connection connection, CloseReason closeReason, org.glassfish.grizzly.Connection grizzlyConnection) {
            this.connection = connection;
            this.closeReason = closeReason;
            this.grizzlyConnection = grizzlyConnection;
        }

        public void execute() {
            this.connection.close(this.closeReason);
            GrizzlyServerFilter.TYRUS_CONNECTION.remove(this.grizzlyConnection);
            GrizzlyServerFilter.TASK_PROCESSOR.remove(this.grizzlyConnection);
        }
    }

    private class ProcessTask extends TaskProcessor.Task {
        private final ByteBuffer buffer;
        private final ReadHandler readHandler;

        private ProcessTask(ByteBuffer buffer, ReadHandler readHandler) {
            this.buffer = buffer;
            this.readHandler = readHandler;
        }

        public void execute() {
            this.readHandler.handle(this.buffer);
        }
    }
}
