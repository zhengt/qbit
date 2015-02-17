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

package io.advantageous.qbit.http.request;

import io.advantageous.qbit.message.Request;
import io.advantageous.qbit.util.MultiMap;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


/**
 * This represents and HTTP request.
 * <p>
 * Created by rhightower on 10/21/14.
 *
 * @author rhightower
 */
public class HttpRequest implements Request<Object> {

    private final String uri;
    private final String remoteAddress;
    private final MultiMap<String, String> params;
    private final MultiMap<String, String> headers;
    private final byte[] body;
    private final String contentType;
    private final String method;
    private final HttpResponseReceiver receiver;
    private final long messageId;
    private final long timestamp;
    private volatile boolean handled;

    public HttpRequest(long id, final String uri, final String method, final MultiMap<String, String> params,
                       final MultiMap<String, String> headers,
                       final byte[] body, final String remoteAddress, String contentType, final HttpResponseReceiver response, long timestamp) {

        this.uri = uri;
        this.messageId = id;
        this.params = params;
        this.body = body;
        this.method = method;
        this.contentType = contentType;
        this.receiver = response;
        this.remoteAddress = remoteAddress;
        this.headers = headers;
        this.timestamp = timestamp;
    }

    @Override
    public String address() {
        return uri;
    }

    @Override
    public String returnAddress() {
        return remoteAddress;
    }

    @Override
    public MultiMap<String, String> params() {
        return params;
    }

    @Override
    public MultiMap<String, String> headers() {
        return headers;
    }

    @Override
    public boolean hasParams() {
        return false;
    }

    @Override
    public boolean hasHeaders() {
        return false;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    @Override
    public boolean isHandled() {
        return handled;
    }

    @Override
    public void handled() {
        handled = true;
    }

    @Override
    public long id() {
        return messageId;
    }

    @Override
    public Object body() {
        return body;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public MultiMap<String, String> getParams() {
        return params;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }


    public HttpResponseReceiver<Object> getReceiver() {
        return receiver;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public MultiMap<String, String> getHeaders() {
        return headers;
    }


    public long getMessageId() {
        return messageId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpRequest request = (HttpRequest) o;

        if (messageId != request.messageId) return false;
        if (timestamp != request.timestamp) return false;
        if (!method.equals(request.method)) return false;
        if (!uri.equals(request.uri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    public String getContentType() {

        if (contentType == null) {
            return headers != MultiMap.EMPTY ? headers.get("Content-Type") : "";
        } else {
            return contentType;
        }

    }


    public boolean isJson() {
        return "application/json".equals(contentType);
    }


    @Override
    public String toString() {
        return "HttpRequest{" +
                "uri='" + uri + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", params=" + params +
                ", headers=" + headers +
                ", body=" + Arrays.toString(body) +
                ", contentType='" + contentType + '\'' +
                ", method='" + method + '\'' +
                ", receiver=" + receiver +
                ", messageId=" + messageId +
                ", timestamp=" + timestamp +
                ", handled=" + handled +
                '}';
    }
}
