package de.klaro.reformcloud2.executor.api.common.application;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.application.basic.DefaultInstallableApplication;
import de.klaro.reformcloud2.executor.api.common.utility.annotiations.Nullable;
import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

public interface InstallableApplication extends Nameable {

    TypeToken<DefaultInstallableApplication> TYPE = new TypeToken<DefaultInstallableApplication>() {};

    String url();

    @Nullable ApplicationLoader loader();
}
