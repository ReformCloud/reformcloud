package systems.reformcloud.reformcloud2.shared.event;

import org.junit.jupiter.api.*;
import systems.reformcloud.reformcloud2.executor.api.event.Event;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.event.ListenerContainer;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.event.priority.EventPriority;

import java.util.concurrent.atomic.AtomicInteger;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DefaultEventManagerTest {

    private final EventManager eventManager = new DefaultEventManager();

    @Test
    @Order(1)
    void testRegisterListener() {
        this.eventManager.registerListener(TestListenerOne.class);
        Assertions.assertEquals(1, this.eventManager.getListeners().size());
        this.eventManager.registerListener(TestListenerTwo.class);
        Assertions.assertEquals(1, this.eventManager.getListeners().size());
        this.eventManager.registerListener(new TestListenerTwo(null));
        Assertions.assertEquals(2, this.eventManager.getListeners().size());
    }

    @Test
    @Order(2)
    void testCallEvent() {
        TestEvent event = this.eventManager.callEvent(new TestEvent());
        Assertions.assertEquals(2, event.counter.get());
    }

    @Test
    @Order(3)
    @Timeout(5)
    void testCallEventAsync() {
        TestEvent event = this.eventManager.callEventAsync(new TestEvent()).getUninterruptedly();
        Assertions.assertNotNull(event);
        Assertions.assertEquals(2, event.counter.get());
    }

    @Test
    @Order(4)
    @Timeout(5)
    void testCallEventAsyncClazz() {
        TestEvent event = this.eventManager.callEventAsync(TestEvent.class).getUninterruptedly();
        Assertions.assertNotNull(event);
        Assertions.assertEquals(2, event.counter.get());
    }

    @Test
    @Order(5)
    void testGetListeners() {
        Assertions.assertEquals(2, this.eventManager.getListeners().size());
        for (ListenerContainer listener : this.eventManager.getListeners()) {
            Assertions.assertEquals(TestEvent.class, listener.getTargetEventClass());
        }
    }

    @Test
    @Order(6)
    void testUnregisterListener() {
        Assertions.assertTrue(this.eventManager.getListeners().size() > 0);
        ListenerContainer container = this.eventManager.getListeners().get(0);
        Assertions.assertEquals(TestListenerOne.class, container.getListenerInstance().getClass());
        this.eventManager.unregisterListener(container.getListenerInstance());
        Assertions.assertEquals(1, this.eventManager.getListeners().size());
    }

    @Test
    @Order(7)
    void unregisterAll() {
        this.eventManager.unregisterAll();
        Assertions.assertEquals(0, this.eventManager.getListeners().size());
    }

    public static class TestEvent extends Event {

        public final AtomicInteger counter = new AtomicInteger();
    }

    public static class TestListenerOne {

        @Listener
        public void handle(TestEvent event) {
            Assertions.assertEquals(0, event.counter.getAndIncrement());
        }
    }

    public static class TestListenerTwo {

        public TestListenerTwo(Object ignored) {
        }

        @Listener(priority = EventPriority.LAST)
        public void handle(TestEvent event) {
            Assertions.assertEquals(1, event.counter.getAndIncrement());
        }
    }
}