package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public final class ProcessStoppedEvent extends Event {

  public ProcessStoppedEvent(@Nonnull ProcessInformation processInformation) {
    this.processInformation = processInformation;
  }

  private final ProcessInformation processInformation;

  @Nonnull
  public ProcessInformation getProcessInformation() {
    return processInformation;
  }
}
