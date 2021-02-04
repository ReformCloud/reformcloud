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
package systems.reformcloud.shared.network.packet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.packet.Packet;
import systems.reformcloud.network.packet.query.QueryManager;

import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultQueryManagerTest {

  private final QueryManager queryManager = new DefaultQueryManager();

  @Test
  public void testSendPacketQuery() {
    NetworkChannel dummyChannel = Mockito.mock(NetworkChannel.class);
    Packet dummyPacket = Mockito.mock(Packet.class);
    UUID queryUniqueId = UUID.randomUUID();

    this.queryManager.sendPacketQuery(dummyChannel, queryUniqueId, dummyPacket);
    Assertions.assertTrue(this.queryManager.getWaitingQuery(queryUniqueId).isPresent());
  }
}
