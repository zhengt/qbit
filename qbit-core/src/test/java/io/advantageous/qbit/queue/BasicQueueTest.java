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

package io.advantageous.qbit.queue;

import org.boon.Lists;
import org.boon.core.Sys;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.core.Sys.sleep;

/**
 * Created by Richard on 8/11/14.
 */
public class BasicQueueTest {

    boolean ok;


    @Test
    public void testUsingListener() {

        final QueueBuilder builder = new QueueBuilder().setName("test").setPollWait(1000).setBatchSize(10);
        Queue<String> queue = builder.build();

        //new BasicQueue<>("test", 1000, TimeUnit.MILLISECONDS, 10);

        final int[] counter = new int[1];

        queue.startListener(new ReceiveQueueListener<String>() {
            @Override
            public void receive(String item) {
                puts(item);
                synchronized (counter) {
                    counter[0]++;
                }
            }

            @Override
            public void empty() {
                puts("Queue is empty");

            }

            @Override
            public void limit() {

                puts("Batch size limit is reached");
            }

            @Override
            public void shutdown() {

                puts("Queue is shut down");
            }

            @Override
            public void idle() {

                puts("Queue is idle");

            }
        });

        final SendQueue<String> sendQueue = queue.sendQueue();
        for (int index = 0; index < 10; index++) {
            sendQueue.send("item" + index);
        }


        sendQueue.flushSends();

        sleep(100);
        synchronized (counter) {
            puts("1", counter[0]);
        }


        for (int index = 0; index < 100; index++) {
            sendQueue.send("item2nd" + index);
        }

        sendQueue.flushSends();


        sleep(100);
        synchronized (counter) {
            puts("2", counter[0]);
        }

        for (int index = 0; index < 5; index++) {
            sleep(100);
            sendQueue.send("item3rd" + index);
        }
        sendQueue.flushSends();

        sleep(100);
        synchronized (counter) {
            puts("3", counter[0]);
        }


        sendQueue.sendMany("hello", "how", "are", "you");


        sleep(100);
        synchronized (counter) {
            puts("4", counter[0]);
        }

        List<String> list = Lists.linkedList("Good", "Thanks");

        sendQueue.sendBatch(list);


        sleep(100);
        synchronized (counter) {
            puts("1", counter[0]);
        }


        sleep(100);
        synchronized (counter) {
            ok = counter[0] == 121 || die("Crap not 121", counter[0]);
        }


        queue.stop();

    }


    @Test
    public void testUsingInput() throws Exception {


        final QueueBuilder builder = new QueueBuilder().setName("test").setPollWait(1000).setBatchSize(10);
        Queue<String> queue = builder.build();

        final int count[] = new int[1];


        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {


                final SendQueue<String> sendQueue = queue.sendQueue();

                for (int index = 0; index < 1000; index++) {
                    sendQueue.send("item" + index);
                }
                sendQueue.flushSends();
            }
        });


        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                ReceiveQueue<String> receiveQueue = queue.receiveQueue();

                while (receiveQueue.poll() != null) {
                    count[0]++;
                }
            }
        });

        writer.start();

        Sys.sleep(100);

        reader.start();

        writer.join();
        reader.join();

        puts(count[0]);

        ok = count[0] == 1000 || die("count should be 1000", count[0]);

    }

    @Test
    public void testUsingInputTake() throws Exception {


        final QueueBuilder builder = new QueueBuilder().setName("test").setPollWait(1000).setBatchSize(10);
        Queue<String> queue = builder.build();

        final AtomicLong count = new AtomicLong();

        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {

                long cnt = 0;
                final ReceiveQueue<String> receiveQueue = queue.receiveQueue();
                String item = receiveQueue.take();

                while (item != null) {
                    cnt++;
                    puts(item);
                    item = receiveQueue.take();

                    if (cnt >= 900) {
                        count.set(cnt);
                        break;
                    }
                }
            }
        });


        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {

                final SendQueue<String> sendQueue = queue.sendQueue();

                for (int index = 0; index < 1000; index++) {
                    sendQueue.send("this item " + index);
                }
                sendQueue.flushSends();
            }
        });


        writer.start();


        reader.start();

        writer.join();
        reader.join();

        puts(count.get());

        ok = count.get() == 900 || die("count should be 1000", count.get());

    }


    @Test
    public void testUsingInputPollWait() throws Exception {


        final QueueBuilder builder = new QueueBuilder().setName("test").setPollWait(1000).setBatchSize(10);
        Queue<String> queue = builder.build();

        final int count[] = new int[1];


        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {


                SendQueue<String> sendQueue = queue.sendQueue();
                for (int index = 0; index < 1000; index++) {
                    sendQueue.send("item" + index);
                }
                sendQueue.flushSends();
            }
        });


        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                ReceiveQueue<String> receiveQueue = queue.receiveQueue();

                String item = receiveQueue.pollWait();

                while (item != null) {
                    count[0]++;
                    puts(item);
                    item = receiveQueue.pollWait();

                }
            }
        });

        writer.start();


        reader.start();

        writer.join();
        reader.join();

        puts(count[0]);

        ok = count[0] == 1000 || die("count should be 1000", count[0]);

    }

}
