package systems.reformcloud.reformcloud2.signs.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

import java.util.concurrent.atomic.AtomicReference;

public interface SignSystemAdapter<T> {

    String table = "reformcloud_internal_db_signs";

    // ====

    AtomicReference<SignSystemAdapter<?>> instance = new AtomicReference<>();

    static SignSystemAdapter<?> getInstance() {
        return instance.get();
    }

    // ====

    /**
     * Handles a process start to the signs
     *
     * @param processInformation The process info of the process which started
     */
    void handleProcessStart(@NotNull ProcessInformation processInformation);

    /**
     * Handles a process update to the signs
     *
     * @param processInformation The process info which should get updated
     */
    void handleProcessUpdate(@NotNull ProcessInformation processInformation);

    /**
     * Handles a process stop to the signs
     *
     * @param processInformation The process info of the process which stopped
     */
    void handleProcessStop(@NotNull ProcessInformation processInformation);

    /**
     * Creates a new sign
     *
     * @param t     The object of the current implementation of a sign
     * @param group The group for which the sign should be
     * @return The created cloud sign or the which already exists
     */
    @NotNull
    CloudSign createSign(@NotNull T t, @NotNull String group);

    /**
     * Deletes a sign
     *
     * @param location The cloud location of the sign which should get deleted
     */
    void deleteSign(@NotNull CloudLocation location);

    /**
     * Deletes all signs
     */
    void deleteAll();

    /**
     * Deletes all signs on which location are not
     */
    void cleanSigns();

    /**
     * Gets a sign at a current location
     *
     * @param location The location of the sign
     * @return The sign at the location or {@code null} if the sign does not exists
     */
    @Nullable
    CloudSign getSignAt(@NotNull CloudLocation location);

    /**
     * @return The converter for all objects from the implementation to the cloud
     */
    @NotNull
    SignConverter<T> getSignConverter();

    /**
     * Checks if a user can connect to the process which is associated with the sign
     *
     * @param cloudSign The sign for which the check should be made
     * @return If a user can connect to the process
     */
    boolean canConnect(@NotNull CloudSign cloudSign);

    // ===================================
    // The following methods are not documented because they are for internal use only
    // ===================================

    @ApiStatus.Internal
    void handleInternalSignCreate(@NotNull CloudSign cloudSign);

    @ApiStatus.Internal
    void handleInternalSignDelete(@NotNull CloudSign cloudSign);

    @ApiStatus.Internal
    void handleSignConfigUpdate(@NotNull SignConfig config);
}
