package systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic;

import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.SetupQuestion;

public final class DefaultSetupQuestion implements SetupQuestion {

  public DefaultSetupQuestion(String question, String wrongAnswer,
                              Predicate<String> predicate,
                              Consumer<String> then) {
    this.question = question;
    this.wrongAnswer = wrongAnswer;
    this.predicate = predicate;
    this.then = then;
  }

  private final String question;

  private final String wrongAnswer;

  private final Predicate<String> predicate;

  private final Consumer<String> then;

  @Nonnull
  @Override
  public String question() {
    return question;
  }

  @Nonnull
  @Override
  public String wrongAnswerMessage() {
    return wrongAnswer;
  }

  @Nonnull
  @Override
  public Predicate<String> tester() {
    return predicate;
  }

  @Nonnull
  @Override
  public Consumer<String> then() {
    return then;
  }
}
