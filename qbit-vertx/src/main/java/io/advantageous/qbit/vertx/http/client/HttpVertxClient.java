/*******************************************************************************
  * Copyright (c) 2015. Rick Hightower, Geoff Chandler
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *  		http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *  ________ __________.______________
  *  \_____  \\______   \   \__    ___/
  *   /  / \  \|    |  _/   | |    |  ______
  *  /   \_/.  \    |   \   | |    | /_____/
  *  \_____\ \_/______  /___| |____|
  *         \__>      \/
  *  ___________.__                  ____.                        _____  .__                                             .__
  *  \__    ___/|  |__   ____       |    |____ ___  _______      /     \ |__| ___________  ____  ______ ______________  _|__| ____  ____
  *    |    |   |  |  \_/ __ \      |    \__  \\  \/ /\__  \    /  \ /  \|  |/ ___\_  __ \/  _ \/  ___// __ \_  __ \  \/ /  |/ ___\/ __ \
  *    |    |   |   Y  \  ___/  /\__|    |/ __ \\   /  / __ \_ /    Y    \  \  \___|  | \(  <_> )___ \\  ___/|  | \/\   /|  \  \__\  ___/
  *    |____|   |___|  /\___  > \________(____  /\_/  (____  / \____|__  /__|\___  >__|   \____/____  >\___  >__|    \_/ |__|\___  >___  >
  *                  \/     \/                \/           \/          \/        \/                 \/     \/                    \/    \/
  *  .____    ._____.
  *  |    |   |__\_ |__
  *  |    |   |  || __ \
  *  |    |___|  || \_\ \
  *  |_______ \__||___  /
  *          \/       \/
  *       ____. _________________    _______         __      __      ___.     _________              __           __      _____________________ ____________________
  *      |    |/   _____/\_____  \   \      \       /  \    /  \ ____\_ |__  /   _____/ ____   ____ |  | __ _____/  |_    \______   \_   _____//   _____/\__    ___/
  *      |    |\_____  \  /   |   \  /   |   \      \   \/\/   // __ \| __ \ \_____  \ /  _ \_/ ___\|  |/ // __ \   __\    |       _/|    __)_ \_____  \   |    |
  *  /\__|    |/        \/    |    \/    |    \      \        /\  ___/| \_\ \/        (  <_> )  \___|    <\  ___/|  |      |    |   \|        \/        \  |    |
  *  \________/_______  /\_______  /\____|__  / /\    \__/\  /  \___  >___  /_______  /\____/ \___  >__|_ \\___  >__| /\   |____|_  /_______  /_______  /  |____|
  *                   \/         \/         \/  )/         \/       \/    \/        \/            \/     \/    \/     )/          \/        \/        \/
  *  __________           __  .__              __      __      ___.
  *  \______   \ ____   _/  |_|  |__   ____   /  \    /  \ ____\_ |__                                                                                               
  *  |    |  _// __ \  \   __\  |  \_/ __ \  \   \/\/   // __ \| __ \
  *   |    |   \  ___/   |  | |   Y  \  ___/   \        /\  ___/| \_\ \
  *   |______  /\___  >  |__| |___|  /\___  >   \__/\  /  \___  >___  /
  *          \/     \/             \/     \/         \/       \/    \/
  *
  * QBit - The Microservice lib for Java : JSON, WebSocket, REST. Be The Web!
  *  http://rick-hightower.blogspot.com/2014/12/rise-of-machines-writing-high-speed.html
  *  http://rick-hightower.blogspot.com/2014/12/quick-guide-to-programming-services-in.html
  *  http://rick-hightower.blogspot.com/2015/01/quick-start-qbit-programming.html
  *  http://rick-hightower.blogspot.com/2015/01/high-speed-soa.html
  *  http://rick-hightower.blogspot.com/2015/02/qbit-event-bus.html
  ******************************************************************************/

package io.advantageous.qbit.vertx.http.client;

import io.advantageous.qbit.GlobalConstants;
import io.advantageous.qbit.concurrent.ExecutorContext;
import io.advantageous.qbit.http.client.HttpClient;
import io.advantageous.qbit.http.request.HttpRequest;
import io.advantageous.qbit.http.websocket.WebSocket;
import io.advantageous.qbit.http.websocket.WebSocketSender;
import io.advantageous.qbit.network.NetSocket;
import io.advantageous.qbit.util.MultiMap;
import io.advantageous.qbit.vertx.MultiMapWrapper;
import org.boon.Str;
import org.boon.core.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpHeaders;

import java.net.ConnectException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static io.advantageous.qbit.concurrent.ScheduledExecutorBuilder.scheduledExecutorBuilder;
import static io.advantageous.qbit.http.websocket.WebSocketBuilder.webSocketBuilder;
import static org.boon.Boon.puts;
import static org.boon.Boon.sputs;

/**
 * @author  rhightower on 1/30/15.
 */
public class HttpVertxClient implements HttpClient {

    protected final boolean keepAlive;
    protected final boolean pipeline;
    protected final int flushInterval;
    private final Logger logger = LoggerFactory.getLogger(HttpVertxClient.class);
    private final boolean debug = false || logger.isDebugEnabled() || GlobalConstants.DEBUG;
    /**
     * Are we closed.
     */
    private final AtomicBoolean closed = new AtomicBoolean();
    /**
     * I am leaving these protected and non-final so subclasses can use injection frameworks for them.
     */
    protected int port;
    protected int requestBatchSize = 50;
    protected String host;
    protected int timeOutInMilliseconds;
    protected int poolSize;
    protected org.vertx.java.core.http.HttpClient httpClient;
    protected Vertx vertx;
    volatile long responseCount = 0;
    private ExecutorContext executorContext;
    private boolean autoFlush;
    private Consumer<Void> periodicFlushCallback = aVoid -> {
    };

    public HttpVertxClient(String host, int port, int requestBatchSize, int timeOutInMilliseconds, int poolSize,
                           boolean autoFlush, int flushInterval, boolean keepAlive, boolean pipeline) {

        this.flushInterval = flushInterval;
        this.port = port;
        this.host = host;
        this.timeOutInMilliseconds = timeOutInMilliseconds;
        this.poolSize = poolSize;
        this.vertx = VertxFactory.newVertx();
        this.requestBatchSize = requestBatchSize;
        this.poolSize = poolSize;
        this.keepAlive = keepAlive;
        this.pipeline = pipeline;
        this.autoFlush = autoFlush;

    }

    @Override
    public void sendHttpRequest(final HttpRequest request) {
        if (debug) logger.debug("HTTP CLIENT: sendHttpRequest:: \n{}\n", request);


        final HttpClientRequest httpClientRequest = httpClient.request(
                request.getMethod(), request.getUri(),
                httpClientResponse -> handleResponse(request, httpClientResponse));

        final MultiMap<String, String> headers = request.getHeaders();

        if (headers != null) {

            for (String key : headers.keySet()) {
                httpClientRequest.putHeader(key, headers.getAll(key));
            }
        }

        final byte[] body = request.getBody();

        if (keepAlive) {
            httpClientRequest.putHeader(HttpHeaders.CONNECTION, HttpHeaders.KEEP_ALIVE);
        }


        if (body != null && body.length > 0) {


            httpClientRequest.putHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(body.length));
            if (request.getContentType() != null) {


                httpClientRequest.putHeader("Content-Type", request.getContentType());
            }
            httpClientRequest.end(new Buffer(request.getBody()));

        } else {
            httpClientRequest.end();
        }

        if (debug) logger.debug("HttpClientVertx::SENT \n{}", request);

    }

    @Override
    public void periodicFlushCallback(Consumer<Void> periodicFlushCallback) {
        this.periodicFlushCallback = periodicFlushCallback;
    }

    @Override
    public void stop() {

        if (executorContext != null) {
            executorContext.stop();
            executorContext = null;
        }

        try {
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (Exception ex) {

            logger.warn("problem shutting down vertx httpClient for QBIT Http Client", ex);
        }

    }

    private void autoFlush() {
        periodicFlushCallback.accept(null);
    }

    @Override
    public HttpClient start() {
        connect();
        if (autoFlush) {

            if (executorContext != null) {
                throw new IllegalStateException(sputs("Unable to start up, it is already started"));
            }

            this.executorContext = scheduledExecutorBuilder()
                    .setThreadName("HttpClient")
                    .setInitialDelay(50)
                    .setPeriod(this.flushInterval).setRunnable(() -> autoFlush())
                    .build();

            executorContext.start();
        }
        return this;
    }

    @Override
    public WebSocket createWebSocket(final String uri) {

        final String remoteAddress = Str.add("ws://", host, ":", Integer.toString(port), uri);
        final WebSocket webSocket = webSocketBuilder().setUri(uri).setWebSocketSender(createWebSocketSender(uri))
                .setRemoteAddress(remoteAddress).build();
        return webSocket;

    }

    private WebSocketSender createWebSocketSender(String uri) {
        return new WebSocketSender() {
            volatile org.vertx.java.core.http.WebSocket vertxWebSocket;

            @Override
            public void sendText(String message) {
                vertxWebSocket.writeTextFrame(message);
            }

            @Override
            public void openWebSocket(WebSocket webSocket) {

                httpClient.connectWebsocket(uri, vertxWebSocket -> {
                    this.vertxWebSocket = vertxWebSocket;

                    /* Handle on Message. */
                    vertxWebSocket.dataHandler(
                            buffer -> webSocket.onTextMessage(buffer.toString("UTF-8"))
                    );

                    /* Handle onClose */
                    vertxWebSocket.closeHandler(event -> {
                        webSocket.onClose();
                    });

                    /* Handle on Exception. */
                    vertxWebSocket.exceptionHandler(event -> {
                        if (event instanceof Exception) {
                            webSocket.onError((Exception) event);
                        } else {
                            webSocket.onError(new Exception(event));
                        }
                    });

                    /* Handle onOpen. */
                    webSocket.onOpen();

                });
            }

            @Override
            public void open(NetSocket netSocket) {
                openWebSocket((WebSocket) netSocket);
            }

            @Override
            public void sendBytes(byte[] message) {
                vertxWebSocket.writeBinaryFrame(new Buffer(message));
            }
        };
    }

    @Override
    public void flush() {
    }

    private void handleResponse(final HttpRequest request, final HttpClientResponse httpClientResponse) {
        final int statusCode = httpClientResponse.statusCode();
        final MultiMap<String, String> headers = httpClientResponse.headers().size() == 0 ? MultiMap.empty() : new MultiMapWrapper(httpClientResponse.headers());

        if (debug) {
            responseCount++;
            puts("status code", httpClientResponse.statusCode(), responseCount);
        }

        httpClientResponse.bodyHandler(buffer -> {
            final String body = buffer.toString("UTF-8");

            if (debug) {
                puts("got body", "BODY");
            }

            handleResponseFromServer(request, statusCode, headers, body);
        });
    }

    private void handleResponseFromServer(HttpRequest request, int responseStatusCode, MultiMap<String, String> responseHeaders, String body) {
        if (debug) {
            logger.debug("HttpClientVertx::handleResponseFromServer:: request = {}, response status code = {}, \n" +
                    "response headers = {}, body = {}", request, responseStatusCode, responseHeaders, body);
        }
        request.getResponse().response(responseStatusCode, responseHeaders.get("Content-Type"), body);
    }

    private void connect() {
        httpClient = vertx.createHttpClient().setHost(host).setPort(port)
                .setConnectTimeout(timeOutInMilliseconds).setMaxPoolSize(poolSize)
                .setKeepAlive(keepAlive).setPipelining(pipeline)
                .setSoLinger(100)
                .setTCPNoDelay(false)
                .setMaxWebSocketFrameSize(100_000_000)
                .setConnectTimeout(this.timeOutInMilliseconds);


        httpClient.setUsePooledBuffers(true);

        if (debug) logger.debug("HTTP CLIENT: connect:: \nhost {} \nport {}\n", host, port);

        httpClient.exceptionHandler(throwable -> {

            if (throwable instanceof ConnectException) {
                closed.set(true);
            } else {
                logger.error("Unable to connect to " + host + " port " + port, throwable);
            }
        });

        Sys.sleep(100);

    }
}
