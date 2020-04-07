package systems.reformcloud.reformcloud2.executor.api.common.application.updater;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface ApplicationUpdateRepository extends Nameable {

    /**
     * Gets called on application install ( may print update message )
     */
    void fetchOrigin();

    /**
     * @return If a new version of the application is available
     */
    boolean isNewVersionAvailable();

    /**
     * @return The next update for the current application, should only return {@code null}
     * if {@link #isNewVersionAvailable()} is {@code false}
     */
    @Nullable
    ApplicationRemoteUpdate getUpdate();
}
