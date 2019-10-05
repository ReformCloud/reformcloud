package systems.reformcloud.reformcloud2.executor.api.common.application;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultInstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.annotiations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface InstallableApplication extends Nameable {

    TypeToken<DefaultInstallableApplication> TYPE = new TypeToken<DefaultInstallableApplication>() {};

    String url();

    @Nullable
    ApplicationLoader loader();
}
