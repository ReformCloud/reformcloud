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
package systems.refomcloud.reformcloud2.embedded.executor;

import java.util.Objects;
import java.util.UUID;

public abstract class PlayerAPIExecutor {

  private static PlayerAPIExecutor instance;

  public static PlayerAPIExecutor getInstance() {
    return instance;
  }

  public static void setInstance(PlayerAPIExecutor instance) {
    PlayerAPIExecutor.instance = Objects.requireNonNull(instance);
  }

  public abstract void executeSendMessage(UUID player, String message);

  public abstract void executeKickPlayer(UUID player, String message);

  public abstract void executePlaySound(UUID player, String sound, float f1, float f2);

  public abstract void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut);

  public abstract void executePlayEffect(UUID player, String entityEffect);

  public abstract void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch);

  public abstract void executeConnect(UUID player, String server);
}
