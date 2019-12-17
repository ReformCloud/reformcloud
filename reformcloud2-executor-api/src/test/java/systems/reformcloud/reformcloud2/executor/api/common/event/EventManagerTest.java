package systems.reformcloud.reformcloud2.executor.api.common.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.event.priority.EventPriority;

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
    public void postCall() {}
  }
}
