package systems.reformcloud.reformcloud2.executor.api.common.utility.operation;

import systems.reformcloud.reformcloud2.executor.api.common.utility.action.CompleteAction;

import java.util.UUID;

public interface Operation extends CompleteAction {

    UUID identifier();

    boolean isDone();
}
