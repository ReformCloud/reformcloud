package systems.reformcloud.reformcloud2.signs.util.converter;

import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SignConverter<T> {

    /**
     * Converts a cloud sign to the given param sign
     *
     * @param cloudSign The sign which should get converted
     * @return The target object
     */
    @Nullable
    T from(@Nonnull CloudSign cloudSign);

    /**
     * Converts a object to a cloud sign
     *
     * @param t The object which should get converted
     * @return The converted cloud sign
     */
    @Nonnull
    CloudSign to(@Nonnull T t, @Nonnull String group);

    /**
     * Converts a object to the current location
     *
     * @param t The object which should get updates
     * @return The created cloud location
     */
    @Nonnull
    CloudLocation to(@Nonnull T t);
}
