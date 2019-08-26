package de.klaro.reformcloud2.executor.api.common.patch;

import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

public interface PatchNote extends Nameable {

    String newVersion();

    String updateMessage();
}
