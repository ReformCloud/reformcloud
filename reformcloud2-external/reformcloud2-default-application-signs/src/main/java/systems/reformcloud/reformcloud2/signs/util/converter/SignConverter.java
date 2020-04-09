package systems.reformcloud.reformcloud2.signs.util.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public interface SignConverter<T> {

    /**
     * Converts a cloud sign to the given param sign
     *
     * @param cloudSign The sign which should get converted
     * @return The target object
     */
    @Nullable
    T from(@NotNull CloudSign cloudSign);

    /**
     * Converts a object to a cloud sign
     *
     * @param t     The object which should get converted
     * @param group The target group of the sign
     * @return The converted cloud sign
     */
    @NotNull
    CloudSign to(@NotNull T t, @NotNull String group);

    /**
     * Converts a object to the current location
     *
     * @param t The object which should get updates
     * @return The created cloud location
     */
    @NotNull
    CloudLocation to(@NotNull T t);
}
