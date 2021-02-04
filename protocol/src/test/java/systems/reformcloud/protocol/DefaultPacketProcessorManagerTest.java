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
package systems.reformcloud.protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import systems.reformcloud.protocol.processor.PacketProcessor;
import systems.reformcloud.protocol.processor.PacketProcessorManager;
import systems.reformcloud.protocol.shared.PacketAuthBegin;
import systems.reformcloud.protocol.shared.PacketAuthSuccess;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DefaultPacketProcessorManagerTest {

  private final PacketProcessorManager processorManager = new DefaultPacketProcessorManager();

  @Test
  @Order(1)
  void testRegisterProcessor() {
    PacketProcessor<PacketAuthBegin> packetProcessor = Mockito.mock(PacketProcessor.class);
    this.processorManager.registerProcessor(packetProcessor, PacketAuthBegin.class);
  }

  @Test
  @Order(2)
  void testGetPacketProcessor() {
    Assertions.assertTrue(this.processorManager.getPacketProcessor(PacketAuthBegin.class).isPresent());
    Assertions.assertFalse(this.processorManager.getPacketProcessor(PacketAuthSuccess.class).isPresent());
  }
}
