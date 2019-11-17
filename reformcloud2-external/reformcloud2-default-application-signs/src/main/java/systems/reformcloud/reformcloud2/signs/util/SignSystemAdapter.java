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

    AtomicReference<SignSystemAdapter> instance = new AtomicReference<>();

    static SignSystemAdapter getInstance() {
        return instance.get();
    }

    // ====

    void handleProcessStart(@Nonnull ProcessInformation processInformation);

    void handleProcessUpdate(@Nonnull ProcessInformation processInformation);

    void handleProcessStop(@Nonnull ProcessInformation processInformation);

    @Nonnull
    CloudSign createSign(@Nonnull T t, @Nonnull String group);

    void deleteSign(@Nonnull CloudLocation location);

    @Nullable
    CloudSign getSignAt(@Nonnull CloudLocation location);

    @Nonnull
    SignConverter<T> getSignConverter();

    void handleInternalSignCreate(@Nonnull CloudSign cloudSign);

    void handleInternalSignDelete(@Nonnull CloudSign cloudSign);

    void handleSignConfigUpdate(@Nonnull SignConfig config);
}
