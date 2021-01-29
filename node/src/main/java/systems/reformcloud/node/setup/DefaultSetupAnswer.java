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
package systems.reformcloud.node.setup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.utility.MoreCollections;
import systems.reformcloud.shared.parser.Parsers;

import java.util.Set;
import java.util.function.Function;

public class DefaultSetupAnswer implements SetupAnswer {

  private static final Set<String> NO_ANSWERS = MoreCollections.set("false", "no");
  private static final Set<String> YES_ANSWERS = MoreCollections.set("true", "yes");

  private final String originalAnswer;

  public DefaultSetupAnswer(String originalAnswer) {
    this.originalAnswer = originalAnswer;
  }

  @Override
  public @NotNull String getOriginalAnswer() {
    return this.originalAnswer;
  }

  @Override
  public @Nullable Integer getAsInt() {
    return Parsers.INT.parse(this.originalAnswer);
  }

  @Override
  public @Nullable Long getAsLong() {
    return Parsers.LONG.parse(this.originalAnswer);
  }

  @Override
  public @Nullable Boolean getAsBoolean() {
    if (YES_ANSWERS.contains(this.originalAnswer.toLowerCase())) {
      return true;
    } else if (NO_ANSWERS.contains(this.originalAnswer.toLowerCase())) {
      return false;
    } else {
      return null;
    }
  }

  @Override
  public <T> @Nullable T getAsObject(@NotNull Function<String, T> mapper) {
    return mapper.apply(this.originalAnswer);
  }
}
