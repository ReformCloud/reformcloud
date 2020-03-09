package systems.reformcloud.reformcloud2.signs.util;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    void handleProcessStart(@Nonnull ProcessInformation processInformation);

    /**
     * Handles a process update to the signs
     *
     * @param processInformation The process info which should get updated
     */
    void handleProcessUpdate(@Nonnull ProcessInformation processInformation);

    /**
     * Handles a process stop to the signs
     *
     * @param processInformation The process info of the process which stopped
     */
    void handleProcessStop(@Nonnull ProcessInformation processInformation);

    /**
     * Creates a new sign
     *
     * @param t The object of the current implementation of a sign
     * @param group The group for which the sign should be
     * @return The created cloud sign or the which already exists
     */
    @Nonnull
    CloudSign createSign(@Nonnull T t, @Nonnull String group);

    /**
     * Deletes a sign
     *
     * @param location The cloud location of the sign which should get deleted
     */
    void deleteSign(@Nonnull CloudLocation location);

    /**
     * Gets a sign at a current location
     *
     * @param location The location of the sign
     * @return The sign at the location or {@code null} if the sign does not exists
     */
    @Nullable
    CloudSign getSignAt(@Nonnull CloudLocation location);

    /**
     * @return The converter for all objects from the implementation to the cloud
     */
    @Nonnull
    SignConverter<T> getSignConverter();

    boolean canConnect(@Nonnull CloudSign cloudSign);

    // ===================================
    // The following methods are not documented because they are for internal use only
    // ===================================

    void handleInternalSignCreate(@Nonnull CloudSign cloudSign);

    void handleInternalSignDelete(@Nonnull CloudSign cloudSign);

    void handleSignConfigUpdate(@Nonnull SignConfig config);
}
