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
package systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.SetupQuestion;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class DefaultSetupQuestion implements SetupQuestion {

    private final String question;
    private final String wrongAnswer;
    private final Predicate<String> predicate;
    private final Consumer<String> then;

    public DefaultSetupQuestion(String question, String wrongAnswer, Predicate<String> predicate, Consumer<String> then) {
        this.question = question;
        this.wrongAnswer = wrongAnswer;
        this.predicate = predicate;
        this.then = then;
    }

    @NotNull
    @Override
    public String question() {
        return this.question;
    }

    @NotNull
    @Override
    public String wrongAnswerMessage() {
        return this.wrongAnswer;
    }

    @NotNull
    @Override
    public Predicate<String> tester() {
        return this.predicate;
    }

    @NotNull
    @Override
    public Consumer<String> then() {
        return this.then;
    }
}
