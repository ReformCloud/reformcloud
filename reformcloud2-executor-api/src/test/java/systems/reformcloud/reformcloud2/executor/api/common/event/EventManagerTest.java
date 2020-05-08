/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.common.event;

import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.event.priority.EventPriority;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventManagerTest {

    private final CountDownLatch countDownLatch = new CountDownLatch(12);

    @Test
    public void testPriority() {
        EventManager eventManager = new DefaultEventManager();
        Event event = new EventTest();

        ListenerTest listenerTest = new ListenerTest();
        ListenerTest2 listenerTest2 = new ListenerTest2();

        assertNotNull(eventManager);
        assertNotNull(listenerTest);
        assertNotNull(listenerTest2);
        assertNotNull(event);

        eventManager.registerListener(listenerTest);
        eventManager.registerListener(listenerTest2);

        assertEquals(1, eventManager.getListeners().size());
        eventManager.callEvent(event);
        assertEquals(7, countDownLatch.getCount());
        eventManager.unregisterListener(listenerTest);
        assertEquals(1, eventManager.getListeners().size());
        eventManager.unregisterAll();
        assertEquals(0, eventManager.getListeners().size());
    }

    public final class ListenerTest {

        @Listener(priority = EventPriority.FIRST)
        public void handle(EventTest eventTest) {
            countDownLatch.countDown();
        }

        @Listener(priority = EventPriority.SECOND)
        public void handleSecond(EventTest eventTest) {
            countDownLatch.countDown();
        }
    }

    public final class ListenerTest2 {

        @Listener(priority = EventPriority.MONITOR)
        public void handle(EventTest eventTest) {
            countDownLatch.countDown();
        }

        @Listener(priority = EventPriority.PENULTIMATE)
        public void handleSecond(EventTest eventTest) {
            countDownLatch.countDown();
        }
    }

    public final class EventTest extends Event {

        @Override
        public void preCall() {
            assertEquals(12, countDownLatch.getCount());
            countDownLatch.countDown();
        }

        @Override
        public void postCall() {

        }
    }

}
