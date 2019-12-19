package systems.reformcloud.reformcloud2.executor.api.common.language.language.source;

import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface LanguageSource extends Nameable {

  /**
   * @return The prefix of the language source
   */
  String getSource();
}
