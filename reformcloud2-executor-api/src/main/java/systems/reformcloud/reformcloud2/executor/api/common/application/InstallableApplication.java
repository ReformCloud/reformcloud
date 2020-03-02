package systems.reformcloud.reformcloud2.executor.api.common.application;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultInstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;

/**
 * Represents an application which can get installed
 */
public interface InstallableApplication extends Nameable {

    TypeToken<DefaultInstallableApplication> TYPE = new TypeToken<DefaultInstallableApplication>() {
    };

    /**
     * @return The download url of the application
     */
    @Nonnull
    String url();
}
