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
package systems.reformcloud.node.process.screen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.language.TranslationHolder;
import systems.reformcloud.network.channel.manager.ChannelManager;
import systems.reformcloud.node.NodeExecutor;
import systems.reformcloud.node.process.DefaultNodeLocalProcessWrapper;
import systems.reformcloud.node.protocol.NodeToNodeProcessScreenLines;
import systems.reformcloud.process.ProcessInformation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultProcessScreen implements ProcessScreen {

  private static final int MAX_CACHE_SIZE = Integer.getInteger("systems.reformcloud.screen-cache-max-size", 256);

  private final Queue<String> cachedLogLines = new ConcurrentLinkedQueue<>();
  private final Collection<String> listeningNodes = new CopyOnWriteArrayList<>();

  private final byte[] readBuffer = new byte[1024];
  private final StringBuffer stringBuffer = new StringBuffer();
  private final Lock readLock = new ReentrantReadWriteLock().readLock();

  private final DefaultNodeLocalProcessWrapper processWrapper;

  public DefaultProcessScreen(DefaultNodeLocalProcessWrapper processWrapper) {
    this.processWrapper = processWrapper;
  }

  @Override
  public @NotNull ProcessInformation getTargetProcess() {
    return this.processWrapper.getProcessInformation();
  }

  @Override
  public @NotNull Queue<String> getCachedLogLines() {
    return this.cachedLogLines;
  }

  @Override
  public @NotNull @UnmodifiableView Collection<String> getListeningNodes() {
    return Collections.unmodifiableCollection(this.listeningNodes);
  }

  @Override
  public void addListeningNode(@NotNull String name) {
    this.listeningNodes.add(name);
  }

  @Override
  public void removeListeningNode(@NotNull String name) {
    this.listeningNodes.remove(name);
  }

  @Override
  public void tick() {
    try {
      this.readLock.lock();
      this.tick0();
    } finally {
      this.readLock.unlock();
    }
  }

  private void tick0() {
    this.processWrapper.getProcess().ifPresent(process -> {
      Collection<String> lines = this.readInputStream(process.getInputStream());
      this.printLines(lines);
      lines = this.readInputStream(process.getErrorStream());
      this.printLines(lines);
    });
  }

  private @NotNull Collection<String> readInputStream(@NotNull InputStream inputStream) {
    try {
      int length;
      while (inputStream.available() > 0 && (length = inputStream.read(this.readBuffer, 0, this.readBuffer.length)) != -1) {
        this.stringBuffer.append(new String(this.readBuffer, 0, length, StandardCharsets.UTF_8));
      }

      String string = this.stringBuffer.toString();
      if (!string.contains("\n") && !string.contains("\r")) {
        return Collections.emptyList();
      }

      Collection<String> lines = new ArrayList<>();
      for (String s : string.split("\r")) {
        for (String s1 : s.split("\n")) {
          if (!s1.trim().isEmpty()) {
            this.cache(s1);
            lines.add(s1);
          }
        }
      }

      this.stringBuffer.setLength(0);
      return lines;
    } catch (IOException exception) {
      if (exception.getMessage() == null || !exception.getMessage().equals("Stream closed")) {
        exception.printStackTrace();
      }
    }

    return Collections.emptyList();
  }

  private void cache(@NotNull String text) {
    while (this.cachedLogLines.size() > MAX_CACHE_SIZE) {
      this.cachedLogLines.poll();
    }

    this.cachedLogLines.add(text);
  }

  private void printLines(@NotNull Collection<String> lines) {
    for (String listeningNode : this.listeningNodes) {
      if (NodeExecutor.getInstance().isOwnIdentity(listeningNode)) {
        for (String line : lines) {
          System.out.println(TranslationHolder.translate(
            "screen-line-added",
            this.processWrapper.getProcessInformation().getName(),
            NodeExecutor.getInstance().getCurrentNodeInformation().getName(),
            line
          ));
        }

        continue;
      }

      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class)
        .getChannel(listeningNode)
        .ifPresent(channel -> channel.sendPacket(new NodeToNodeProcessScreenLines(
          this.processWrapper.getProcessInformation().getName(),
          NodeExecutor.getInstance().getSelfName(),
          lines
        )));
    }
  }
}
