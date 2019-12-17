package systems.reformcloud.reformcloud2.executor.api.common.logger.setup;

import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public interface SetupQuestion {

  /**
   * @return The question as string
   */
  @Nonnull String question();

  /**
   * @return The message which should get printed if the current input made to
   *     the console is invalid
   */
  @Nonnull String wrongAnswerMessage();

  /**
   * @return The {@link Predicate} which should test if the given answer is
   *     correct
   */
  @Nonnull Predicate<String> tester();

  /**
   * @return The {@link Consumer} which should handle the correct input
   */
  @Nonnull Consumer<String> then();
}
