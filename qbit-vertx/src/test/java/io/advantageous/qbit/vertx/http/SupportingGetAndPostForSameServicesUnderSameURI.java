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

package io.advantageous.qbit.vertx.http;

import io.advantageous.qbit.annotation.RequestMapping;
import io.advantageous.qbit.annotation.RequestMethod;
import io.advantageous.qbit.client.Client;
import io.advantageous.qbit.client.ClientBuilder;
import io.advantageous.qbit.http.client.HttpClient;
import io.advantageous.qbit.http.client.HttpClientBuilder;
import io.advantageous.qbit.http.request.HttpRequest;
import io.advantageous.qbit.http.request.HttpRequestBuilder;
import io.advantageous.qbit.http.request.HttpTextReceiver;
import io.advantageous.qbit.server.ServiceServer;
import io.advantageous.qbit.server.ServiceServerBuilder;
import io.advantageous.qbit.service.Callback;
import io.advantageous.qbit.service.ServiceProxyUtils;
import org.boon.core.Sys;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by rhightower on 1/19/15.
 */
public class SupportingGetAndPostForSameServicesUnderSameURI {

    Client client;
    ServiceServer server;
    HttpClient httpClient;
    ClientServiceInterface clientProxy;
    volatile int callCount;
    AtomicReference<String> pongValue;
    boolean ok;
    int port = 8888;

    @Test
    public void testWebSocket() throws Exception {

        clientProxy.ping(new Callback<String>() {
            @Override
            public void accept(String s) {
                puts(s);
                pongValue.set(s);
            }
        }, "hi");

        ServiceProxyUtils.flushServiceProxy(clientProxy);


        while (pongValue.get() == null) {
            Sys.sleep(100);
        }

        final String pongValue = this.pongValue.get();
        ok = pongValue.equals("hi pong") || die();

    }

    @Test
    public void testRestCallSimple() throws Exception {

        final HttpRequest request = new HttpRequestBuilder()
                .setUri("/services/pinger/ping")
                .setJsonBodyForPost("\"hello\"")
                .setTextReceiver(new HttpTextReceiver() {
                    @Override
                    public void response(int code, String mimeType, String body) {
                        if (code == 200) {
                            pongValue.set(body);
                        } else {
                            pongValue.set("ERROR " + body);
                            throw new RuntimeException("ERROR " + code + " " + body);

                        }
                    }
                })
                .build();

        httpClient.sendHttpRequest(request);

        httpClient.flush();


        while (pongValue.get() == null) {
            Sys.sleep(100);
        }


        final String pongValue = this.pongValue.get();
        ok = pongValue.equals("\"hello pong\"") || die(pongValue);

    }

    @Test
    public void testRestCallSimpleGET() throws Exception {

        final HttpRequest request = new HttpRequestBuilder()
                .setUri("/services/pinger/ping")
                .setMethod("GET")
                .setTextReceiver(new HttpTextReceiver() {
                    @Override
                    public void response(int code, String mimeType, String body) {
                        if (code == 200) {
                            pongValue.set(body);
                        } else {
                            pongValue.set("ERROR " + body);
                            throw new RuntimeException("ERROR " + code + " " + body);

                        }
                    }
                })
                .build();

        httpClient.sendHttpRequest(request);

        httpClient.flush();


        while (pongValue.get() == null) {
            Sys.sleep(100);
        }


        final String pongValue = this.pongValue.get();
        ok = pongValue.equals("\"pong\"") || die(pongValue);

    }

    @Before
    public void setup() throws Exception {
        pongValue = new AtomicReference<>();

        httpClient = new HttpClientBuilder().setPort(port).build();

        client = new ClientBuilder().setPort(port).build();
        server = new ServiceServerBuilder().setPort(port).build();

        server.initServices(new MockService());

        server.start();

        Sys.sleep(200);

        clientProxy = client.createProxy(ClientServiceInterface.class, "pinger");
        client.start();
        httpClient.start();

        callCount = 0;
        pongValue.set(null);

        Sys.sleep(200);

        puts("STARTED..........");


    }

    @After
    public void teardown() throws Exception {

        port++;

        if (!ok) {
            die("NOT OK");
        }

        Sys.sleep(200);
        server.stop();
        Sys.sleep(200);
        client.stop();
        httpClient.stop();
        Sys.sleep(200);
        server = null;
        client = null;
        System.gc();
        Sys.sleep(1000);

    }

    static interface ClientServiceInterface {
        String ping(Callback<String> callback, String ping);
    }

    @RequestMapping("/pinger")
    class MockService {

        @RequestMapping(method = RequestMethod.POST, value = "/ping")
        public String ping(String ping) {
            callCount++;
            return ping + " pong";
        }


        @RequestMapping(method = RequestMethod.GET, value = "/ping")
        public String get() {
            callCount++;
            return "pong";
        }
    }
}
