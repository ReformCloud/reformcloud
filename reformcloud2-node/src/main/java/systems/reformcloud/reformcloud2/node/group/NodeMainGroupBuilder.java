/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.node.group;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.groups.main.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.shared.group.DefaultMainGroup;
import systems.reformcloud.reformcloud2.shared.group.DefaultMainGroupBuilder;

public class NodeMainGroupBuilder extends DefaultMainGroupBuilder {

  private final DefaultNodeMainGroupProvider provider;

  NodeMainGroupBuilder(DefaultNodeMainGroupProvider provider) {
    this.provider = provider;
  }

  @NotNull
  @Override
  public Task<MainGroup> create() {
    Conditions.nonNull(super.name, "Unable to create main group with no name provided");
    return Task.supply(() -> {
      if (this.provider.getMainGroup(super.name).isPresent()) {
        return null;
      }

      MainGroup mainGroup = new DefaultMainGroup(super.subGroups, super.name);
      this.provider.addGroup(mainGroup);
      return mainGroup;
    });
  }
}
