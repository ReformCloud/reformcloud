package systems.reformcloud.reformcloud2.executor.api.common.utility.operation;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.utility.action.CompleteAction;

@Deprecated
public interface Operation extends CompleteAction {

  UUID identifier();

  boolean isDone();
}
