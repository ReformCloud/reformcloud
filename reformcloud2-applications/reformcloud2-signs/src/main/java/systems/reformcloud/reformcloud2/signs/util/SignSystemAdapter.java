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
package systems.reformcloud.reformcloud2.signs.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public interface SignSystemAdapter<T> {

  String table = "reformcloud_internal_db_signs";

  // ====

  AtomicReference<SignSystemAdapter<?>> instance = new AtomicReference<>();

  static SignSystemAdapter<?> getInstance() {
    return instance.get();
  }

  // ====

  /**
   * Handles a process start to the signs
   *
   * @param processInformation The process info of the process which started
   */
  void handleProcessStart(@NotNull ProcessInformation processInformation);

  /**
   * Handles a process update to the signs
   *
   * @param processInformation The process info which should get updated
   */
  void handleProcessUpdate(@NotNull ProcessInformation processInformation);

  /**
   * Handles a process stop to the signs
   *
   * @param processInformation The process info of the process which stopped
   */
  void handleProcessStop(@NotNull ProcessInformation processInformation);

  /**
   * Creates a new sign
   *
   * @param t     The object of the current implementation of a sign
   * @param group The group for which the sign should be
   * @return The created cloud sign or the which already exists
   */
  @NotNull
  CloudSign createSign(@NotNull T t, @NotNull String group);

  /**
   * Deletes a sign
   *
   * @param location The cloud location of the sign which should get deleted
   */
  void deleteSign(@NotNull CloudLocation location);

  /**
   * Deletes all signs
   */
  void deleteAll();

  /**
   * Deletes all signs on which location are not
   */
  void cleanSigns();

  /**
   * Gets a sign at a current location
   *
   * @param location The location of the sign
   * @return The sign at the location or {@code null} if the sign does not exists
   */
  @Nullable
  CloudSign getSignAt(@NotNull CloudLocation location);

  /**
   * @return The converter for all objects from the implementation to the cloud
   */
  @NotNull
  SignConverter<T> getSignConverter();

  /**
   * Checks if a user can connect to the process which is associated with the sign
   *
   * @param cloudSign         The sign for which the check should be made
   * @param permissionChecker The permission checker used for permission checks
   * @return If a user can connect to the process
   */
  boolean canConnect(@NotNull CloudSign cloudSign, @NotNull Function<String, Boolean> permissionChecker);

  // ===================================
  // The following methods are not documented because they are for internal use only
  // ===================================

  @ApiStatus.Internal
  void handleInternalSignCreate(@NotNull CloudSign cloudSign);

  @ApiStatus.Internal
  void handleInternalSignDelete(@NotNull CloudSign cloudSign);

  @ApiStatus.Internal
  void handleSignConfigUpdate(@NotNull SignConfig config);
}
