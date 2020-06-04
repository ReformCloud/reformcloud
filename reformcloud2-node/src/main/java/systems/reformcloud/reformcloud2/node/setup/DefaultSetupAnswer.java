/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.node.setup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;

import java.util.function.Function;

public class DefaultSetupAnswer implements SetupAnswer {

    public DefaultSetupAnswer(String originalAnswer) {
        this.originalAnswer = originalAnswer;
    }

    private final String originalAnswer;

    @Override
    public @NotNull String getOriginalAnswer() {
        return this.originalAnswer;
    }

    @Override
    public @Nullable Integer getAsInt() {
        return CommonHelper.fromString(this.originalAnswer);
    }

    @Override
    public @Nullable Long getAsLong() {
        return CommonHelper.longFromString(this.originalAnswer);
    }

    @Override
    public @Nullable Boolean getAsBoolean() {
        Boolean result = CommonHelper.booleanFromString(this.originalAnswer);
        if (result != null) {
            return result;
        }

        if (this.originalAnswer.equalsIgnoreCase("yes") || this.originalAnswer.equalsIgnoreCase("no")) {
            return this.originalAnswer.equalsIgnoreCase("yes");
        }

        return null;
    }

    @Override
    public <T> @Nullable T getAsObject(@NotNull Function<String, T> mapper) {
        return mapper.apply(this.originalAnswer);
    }
}
