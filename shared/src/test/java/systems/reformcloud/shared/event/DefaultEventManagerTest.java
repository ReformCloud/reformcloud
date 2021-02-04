/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.shared.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import systems.reformcloud.event.Event;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.event.ListenerContainer;
import systems.reformcloud.event.handler.Listener;
import systems.reformcloud.event.priority.EventPriority;

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
    Assertions.assertTrue(!this.eventManager.getListeners().isEmpty());
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

  public static class TestEvent implements Event {

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
