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
package systems.reformcloud.node.factory;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.node.process.DefaultNodeProcessProvider;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultProcessFactoryController implements ProcessFactoryController {

  private final Map<String, ProcessFactory> factoriesByName = new ConcurrentHashMap<>();

  public DefaultProcessFactoryController(@NotNull DefaultNodeProcessProvider processProvider) {
    this.registerProcessFactory(new DefaultProcessFactory(processProvider));
  }

  @Override
  public void registerProcessFactory(@NotNull ProcessFactory factory) {
    this.factoriesByName.put(factory.getName(), factory);
  }

  @Override
  public void unregisterFactory(@NotNull ProcessFactory factory) {
    this.unregisterFactoryByName(factory.getName());
  }

  @Override
  public void unregisterFactoryByName(@NotNull String name) {
    this.factoriesByName.remove(name);
  }

  @Override
  public @NotNull Optional<ProcessFactory> getProcessFactoryByName(@NotNull String name) {
    return Optional.ofNullable(this.factoriesByName.get(name));
  }

  @Override
  public @NotNull ProcessFactory getDefaultProcessFactory() {
    for (ProcessFactory value : this.factoriesByName.values()) {
      if (value.isDefault()) {
        return value;
      }
    }

    throw new IllegalStateException("Default process factory not present anymore");
  }
}
