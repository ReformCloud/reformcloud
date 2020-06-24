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

import java.util.function.Function;

public class DefaultSetupQuestion implements SetupQuestion {

    public DefaultSetupQuestion(Function<SetupAnswer, Boolean> answerHandler, String invalidInputMessage, String originalQuestion) {
        this.answerHandler = answerHandler;
        this.invalidInputMessage = invalidInputMessage;
        this.originalQuestion = originalQuestion;
    }

    private final Function<SetupAnswer, Boolean> answerHandler;
    private final String invalidInputMessage;
    private final String originalQuestion;

    @Override
    public @NotNull Function<SetupAnswer, Boolean> getAnswerHandler() {
        return this.answerHandler;
    }

    @Override
    public @NotNull String getInvalidInputMessage() {
        return this.invalidInputMessage;
    }

    @Override
    public @NotNull String getOriginalQuestion() {
        return this.originalQuestion;
    }
}
