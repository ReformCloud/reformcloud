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
package systems.reformcloud.signs.cloudburst.adapter;

import com.nukkitx.math.vector.Vector3i;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.blockentity.Sign;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.signs.util.converter.SignConverter;
import systems.reformcloud.signs.util.sign.CloudLocation;
import systems.reformcloud.signs.util.sign.CloudSign;

public class CloudBurstSignConverter implements SignConverter<Sign> {

  static final CloudBurstSignConverter INSTANCE = new CloudBurstSignConverter();

  @Nullable
  @Override
  public Sign from(@NotNull CloudSign cloudSign) {
    Location location = this.accumulate(cloudSign.getLocation());
    return location != null && location.getLevel().getBlockEntity(location.getBlock().getPosition()) instanceof Sign
      ? (Sign) location.getLevel().getBlockEntity(location.getBlock().getPosition())
      : null;
  }

  @NotNull
  @Override
  public CloudSign to(@NotNull Sign sign, @NotNull String group) {
    return new CloudSign(group, this.accumulate(sign.getLevel(), sign.getPosition()));
  }

  @NotNull
  @Override
  public CloudLocation to(@NotNull Sign sign) {
    return this.accumulate(sign.getLevel(), sign.getPosition());
  }

  private Location accumulate(CloudLocation cloudLocation) {
    if (Server.getInstance().getLevelByName(cloudLocation.getWorld()) == null) {
      return null;
    }

    return Location.from(
      (float) cloudLocation.getX(),
      (float) cloudLocation.getY(),
      (float) cloudLocation.getZ(),
      cloudLocation.getYaw(),
      cloudLocation.getPitch(),
      Server.getInstance().getLevelByName(cloudLocation.getWorld())
    );
  }

  private CloudLocation accumulate(Level level, Vector3i vector3i) {
    return new CloudLocation(
      level.getName(),
      Embedded.getInstance().getCurrentProcessInformation().getProcessGroup().getName(),
      vector3i.getX(),
      vector3i.getY(),
      vector3i.getZ(),
      0,
      0
    );
  }
}
