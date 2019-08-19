package de.klaro.reformcloud2.executor.api.common.event;

import de.klaro.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import de.klaro.reformcloud2.executor.api.common.event.handler.Listener;
import de.klaro.reformcloud2.executor.api.common.event.priority.EventPriority;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventManagerTest {

    private final AtomicInteger counter = new AtomicInteger(0);

    private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    @Test
    public void testSingleListener() {
        EventManager eventManager = new DefaultEventManager();
        Event eventTest = new EventTest();
        ListenerTest listenerTest = new ListenerTest();

        assertNotNull(eventManager);
        assertNotNull(listenerTest);
        assertNotNull(eventTest);

        eventManager.registerListener(listenerTest);

        assertEquals(1, eventManager.getListeners().size());

        eventManager.callEvent(eventTest);

        assertEquals(2, counter.get());

        eventManager.unregisterAll();
        counter.set(0);

        assertEquals(0, eventManager.getListeners().size());
        assertEquals(0, counter.get());

        EventManagerTest.this.atomicBoolean.set(Boolean.FALSE);
        EventManagerTest.this.counter.set(0);
    }

    @Test
    public void testDoubleListener() {
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

        assertEquals(4, counter.get());

        eventManager.unregisterListener(listenerTest);

        assertEquals(1, eventManager.getListeners().size());

        eventManager.unregisterAll();

        assertEquals(0, eventManager.getListeners().size());

        EventManagerTest.this.atomicBoolean.set(Boolean.FALSE);
    }

    public final class ListenerTest {

        @Listener(priority = EventPriority.FIRST)
        public void handle(EventTest eventTest) {
            assertEquals(eventTest.test, "1234");
            eventTest.setTest("5678");

            EventManagerTest.this.counter.getAndIncrement();
            EventManagerTest.this.atomicBoolean.set(Boolean.TRUE);
        }

        @Listener(priority = EventPriority.SECOND)
        public void handleSecond(EventTest eventTest) {
            assertEquals(eventTest.test, "5678");

            eventTest.setTest("1111");
            EventManagerTest.this.counter.getAndIncrement();
        }
    }

    public final class ListenerTest2 {

        @Listener(priority = EventPriority.MONITOR)
        public void handle(EventTest eventTest) {
            assertEquals("1111", eventTest.test);

            eventTest.setTest("1010");
            EventManagerTest.this.counter.getAndIncrement();
            EventManagerTest.this.atomicBoolean.set(Boolean.TRUE);
        }

        @Listener(priority = EventPriority.PENULTIMATE)
        public void handleSecond(EventTest eventTest) {
            assertEquals("1010", eventTest.test);

            EventManagerTest.this.counter.getAndIncrement();
        }
    }

    public final class EventTest extends Event {

        String test = "1234";

        void setTest(String test) {
            this.test = test;
        }

        @Override
        public void preCall() {
            assertEquals(Boolean.FALSE, EventManagerTest.this.atomicBoolean.get());
        }

        @Override
        public void postCall() {
            assertEquals(Boolean.TRUE, EventManagerTest.this.atomicBoolean.get());
        }
    }

}