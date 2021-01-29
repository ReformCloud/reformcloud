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
package systems.refomcloud.reformcloud2.embedded.processors;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.protocol.shared.PacketSendBossBar;

import java.util.Objects;
import java.util.stream.Collectors;

public class PacketSendBossBarProcessor extends PlayerApiToNodePacketProcessor<PacketSendBossBar> {

  @Override
  public void process(@NotNull NetworkChannel channel, @NotNull PacketSendBossBar packet) {
    final BossBar bossBar = BossBar.bossBar(
      GsonComponentSerializer.gson().deserialize(packet.getName()),
      packet.getProgress(),
      EnumUtil.findEnumFieldByIndex(BossBar.Color.class, packet.getColor()).orElseThrow(() -> new RuntimeException("No color index " + packet.getColor())),
      EnumUtil.findEnumFieldByIndex(BossBar.Overlay.class, packet.getOverlay()).orElseThrow(() -> new RuntimeException("No overlay index " + packet.getOverlay())),
      packet.getFlags().stream()
        .map(index -> EnumUtil.findEnumFieldByIndex(BossBar.Flag.class, index).orElse(null))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet())
    );
    if (packet.isHide()) {
      this.getPlayerExecutor().executeHideBossBar(packet.getUniqueId(), bossBar);
    } else {
      this.getPlayerExecutor().executeSendBossBar(packet.getUniqueId(), bossBar);
    }
  }
}
