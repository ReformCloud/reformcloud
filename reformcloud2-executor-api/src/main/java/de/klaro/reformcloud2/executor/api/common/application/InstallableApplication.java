package de.klaro.reformcloud2.executor.api.common.application;

import de.klaro.reformcloud2.executor.api.common.utility.annotiations.Nullable;
import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

public interface InstallableApplication extends Nameable {

    String url();

    @Nullable ApplicationLoader loader();
}
