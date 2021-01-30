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
package systems.reformcloud.node;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.address.NetworkAddress;
import systems.reformcloud.network.data.SerializableObject;
import systems.reformcloud.process.ProcessRuntimeInformation;
import systems.reformcloud.utility.name.Nameable;

import java.util.UUID;

/**
 * An information about a node.
 */
public interface NodeInformation extends Nameable, SerializableObject {

  /**
   * Gets the unique id of the node.
   *
   * @return The unique id of the node.
   */
  @NotNull UUID getUniqueId();

  /**
   * Gets the startup milliseconds of the node.
   *
   * @return The startup milliseconds of the node.
   */
  long getStartupMillis();

  /**
   * Gets the last update milliseconds of the node.
   *
   * @return The last update milliseconds of the node.
   */
  long getLastUpdateTimestamp();

  /**
   * Get the last used memory on the update.
   *
   * @return The last used memory on the update.
   */
  long getUsedMemory();

  /**
   * Get the last max memory on the update.
   *
   * @return The last max memory on the update.
   */
  long getMaxMemory();

  /**
   * Get the process start host on the monitored node.
   *
   * @return The process start host on the monitored node.
   */
  @NotNull NetworkAddress getProcessStartHost();

  /**
   * Get the last process runtime information publish by this node.
   *
   * @return The last process runtime information publish by this node.
   */
  @NotNull ProcessRuntimeInformation getProcessRuntimeInformation();
}
