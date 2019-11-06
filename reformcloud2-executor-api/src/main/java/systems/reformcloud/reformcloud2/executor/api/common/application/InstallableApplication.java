package systems.reformcloud.reformcloud2.executor.api.common.application;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultInstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface InstallableApplication extends Nameable {

    TypeToken<DefaultInstallableApplication> TYPE = new TypeToken<DefaultInstallableApplication>() {};

    @Nonnull
    String url();

    @Nullable
    ApplicationLoader loader();
}
