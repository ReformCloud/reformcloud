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
package systems.reformcloud.reformcloud2.node.setup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.node.NodeExecutor;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultSetup implements Setup {

  private final Queue<SetupQuestion> questions = new ConcurrentLinkedQueue<>();

  @Override
  public @NotNull Setup addQuestion(@NotNull SetupQuestion setupQuestion) {
    this.questions.add(setupQuestion);
    return this;
  }

  @Override
  public @NotNull @UnmodifiableView Collection<SetupQuestion> getQuestions() {
    return this.questions;
  }

  @Override
  public void runSetup() {
    SetupQuestion question;
    while ((question = this.questions.poll()) != null) {
      this.beginQuestion(question);
      this.handleQuestion(question);
      NodeExecutor.getInstance().getConsole().clearScreen();
    }
    NodeExecutor.getInstance().getConsole().clearHistory();
  }

  @Override
  public void clear() {
    this.questions.clear();
  }

  private void handleQuestion(@NotNull SetupQuestion question) {
    System.out.println(question.getOriginalQuestion());
    String answer = NodeExecutor.getInstance().getConsole().readString().getUninterruptedly();
    while (!question.getAnswerHandler().apply(new DefaultSetupAnswer(answer))) {
      System.err.println(question.getInvalidInputMessage());
      answer = NodeExecutor.getInstance().getConsole().readString().getUninterruptedly();
    }
  }

  private void beginQuestion(@NotNull SetupQuestion question) {
    NodeExecutor.getInstance().getConsole().clearHistory();
    for (String historyEntry : question.getHistoryEntries()) {
      NodeExecutor.getInstance().getConsole().addHistoryEntry(historyEntry);
    }
  }
}
