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

package io.advantageous.qbit.vertx.http.server;


import io.advantageous.qbit.GlobalConstants;
import io.advantageous.qbit.http.config.HttpServerOptions;
import io.advantageous.qbit.http.request.HttpRequest;
import io.advantageous.qbit.http.server.HttpServer;
import io.advantageous.qbit.http.server.impl.SimpleHttpServer;
import io.advantageous.qbit.http.server.websocket.WebSocketMessage;
import io.advantageous.qbit.http.websocket.WebSocket;
import io.advantageous.qbit.system.QBitSystemManager;
import io.advantageous.qbit.util.Timer;
import org.boon.Str;
import org.boon.core.reflection.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.ServerWebSocket;

import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 */
public class HttpServerVertx implements HttpServer {

    private final Logger logger = LoggerFactory.getLogger(HttpServerVertx.class);
    private final boolean debug = GlobalConstants.DEBUG || logger.isDebugEnabled();
    private final QBitSystemManager systemManager;
    private final SimpleHttpServer simpleHttpServer;
    private final int port;
    private final String host;
    private final Vertx vertx;
    private final HttpServerOptions options;
    private final VertxServerUtils vertxUtils = new VertxServerUtils();
    private org.vertx.java.core.http.HttpServer httpServer;

    /**
     * For Metrics.
     */
    private volatile int exceptionCount;
    /**
     * For Metrics.
     */
    private volatile int closeCount;


    public HttpServerVertx(final Vertx vertx,
                           final HttpServerOptions options,
                           final QBitSystemManager systemManager) {

        this.simpleHttpServer = new SimpleHttpServer(null, options.getFlushInterval());
        this.vertx = vertx;
        this.systemManager = systemManager;
        this.port = options.getPort();
        this.host = options.getHost();
        this.options = BeanUtils.copy(options);

    }

    public HttpServerVertx(HttpServerOptions options,
                           QBitSystemManager systemManager) {

        this(VertxFactory.newVertx(), options, systemManager);
    }


    @Override
    public void setShouldContinueHttpRequest(final Predicate<HttpRequest> shouldContinueHttpRequest) {
        this.simpleHttpServer.setShouldContinueHttpRequest(shouldContinueHttpRequest);
    }

    @Override
    public void setWebSocketMessageConsumer(final Consumer<WebSocketMessage> webSocketMessageConsumer) {
        this.simpleHttpServer.setWebSocketMessageConsumer(webSocketMessageConsumer);
    }

    @Override
    public void setWebSocketCloseConsumer(final Consumer<WebSocketMessage> webSocketMessageConsumer) {
        this.simpleHttpServer.setWebSocketCloseConsumer(webSocketMessageConsumer);
    }

    @Override
    public void setHttpRequestConsumer(final Consumer<HttpRequest> httpRequestConsumer) {
        this.simpleHttpServer.setHttpRequestConsumer(httpRequestConsumer);
    }

    @Override
    public void setHttpRequestsIdleConsumer(final Consumer<Void> idleRequestConsumer) {
        this.simpleHttpServer.setHttpRequestsIdleConsumer(
                new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) {
                        idleRequestConsumer.accept(null);
                        vertxUtils.setTime(Timer.timer().now());
                    }
                }
        );
    }


    @Override
    public void setWebSocketIdleConsume(final Consumer<Void> idleWebSocketConsumer) {
        this.simpleHttpServer.setWebSocketIdleConsume(
                new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) {
                        idleWebSocketConsumer.accept(null);
                        vertxUtils.setTime(Timer.timer().now());
                    }
                }
        );
    }

    @Override
    public void start() {

        simpleHttpServer.start();

        if (debug) {
            vertx.setPeriodic(10_000, new Handler<Long>() {
                @Override
                public void handle(Long event) {

                    logger.info("Exceptions", exceptionCount, "Close Count", closeCount);
                }
            });
        }
        httpServer = vertx.createHttpServer();

        httpServer.setTCPNoDelay(true);//TODO this needs to be in builder
        httpServer.setSoLinger(0); //TODO this needs to be in builder
        httpServer.setUsePooledBuffers(true); //TODO this needs to be in builder
        httpServer.setReuseAddress(true); //TODO this needs to be in builder
        httpServer.setAcceptBacklog(1_000_000); //TODO this needs to be in builder
        httpServer.setTCPKeepAlive(true); //TODO this needs to be in builder
        httpServer.setCompressionSupported(false);//TODO this needs to be in builder
        httpServer.setMaxWebSocketFrameSize(100_000_000);
        httpServer.websocketHandler(this::handleWebSocketMessage);
        httpServer.requestHandler(this::handleHttpRequest);


        if (Str.isEmpty(host)) {
            httpServer.listen(port);
        } else {
            httpServer.listen(port, host);
        }

        logger.info("HTTP SERVER started on port " + port + " host " + host);

    }


    @Override
    public void stop() {
        simpleHttpServer.stop();
        try {
            if (httpServer != null) {

                httpServer.close();
            }
        } catch (Exception ex) {

            logger.info("HTTP SERVER unable to close " + port + " host " + host);
        }
        if (systemManager != null) systemManager.serviceShutDown();

    }


    private void handleHttpRequest(final HttpServerRequest request) {


        if (debug) {
            setupMetrics(request);
            logger.debug("HttpServerVertx::handleHttpRequest::{}:{}", request.method(), request.uri());
        }

        switch (request.method()) {

            case "PUT":
            case "POST":
                request.bodyHandler((Buffer buffer) -> {
                    final HttpRequest postRequest = vertxUtils.createRequest(request, buffer);

                    simpleHttpServer.handleRequest(postRequest);

                });
                break;


            case "HEAD":
            case "OPTIONS":
            case "DELETE":
            case "GET":
                final HttpRequest getRequest;
                getRequest = vertxUtils.createRequest(request, null);
                simpleHttpServer.handleRequest(getRequest);
                break;

            default:
                throw new IllegalStateException("method not supported yet " + request.method());

        }

    }


    private void setupMetrics(final HttpServerRequest request) {

        request.exceptionHandler(event -> {

            if (debug) {
                exceptionCount++;
            }

            logger.info("EXCEPTION", event);

        });

        request.endHandler(event -> {


            if (debug) {
                closeCount++;
            }


            logger.info("REQUEST OVER");
        });
    }

    private void handleWebSocketMessage(final ServerWebSocket webSocket) {
        simpleHttpServer.handleOpenWebSocket(vertxUtils.createWebSocket(webSocket));
    }


    @Override
    public void setWebSocketOnOpenConsumer(Consumer<WebSocket> onOpenConsumer) {
        this.simpleHttpServer.setWebSocketOnOpenConsumer(onOpenConsumer);
    }


}
