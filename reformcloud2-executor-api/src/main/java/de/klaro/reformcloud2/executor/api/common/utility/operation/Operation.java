package de.klaro.reformcloud2.executor.api.common.utility.operation;

import de.klaro.reformcloud2.executor.api.common.utility.action.CompleteAction;

import java.util.UUID;

public interface Operation extends CompleteAction {

    UUID identifier();

    boolean isDone();
}
