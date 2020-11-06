/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.enums;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class EnumUtil {

    private static final Map<Class<? extends Enum<?>>, Entry> CACHE = new ConcurrentHashMap<>();
    private static final IllegalStateException BASE_NULL = new IllegalStateException("Entry base evaluated null instead of enum set");

    private EnumUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Tries to find an enum field by the given name, using a weak cache
     *
     * @param enumClass The class to find the constant in
     * @param field     The name of the field to find
     * @param <T>       The type of the enum class
     * @return An optional which is empty if no field by the given value was found or containing the field associated with the name
     */
    @NotNull
    public static <T extends Enum<T>> Optional<T> findEnumFieldByName(@NotNull Class<T> enumClass, @NotNull String field) {
        checkClass(enumClass);

        Enum<?> reference = CACHE.get(enumClass).namesToConstant.get(field.toLowerCase());
        return reference == null ? Optional.empty() : Optional.of(enumClass.cast(reference));
    }

    /**
     * Tries to find an enum field by the given ordinal index
     *
     * @param enumClass The class to find the constant in
     * @param ordinal   The ordinal field of the enum constant
     * @param <T>       The type of the enum class
     * @return An optional which is empty if no field by the given value was found or containing the field associated with the ordinal index
     */
    @NotNull
    public static <T extends Enum<T>> Optional<T> findEnumFieldByIndex(@NotNull Class<T> enumClass, int ordinal) {
        checkClass(enumClass);

        Enum<?> reference = CACHE.get(enumClass).indexToConstant.get(ordinal);
        return reference == null ? Optional.empty() : Optional.of(enumClass.cast(reference));
    }

    /**
     * Gets all values of an enum class using a weak cache
     *
     * @param enumClass The class to get all values of
     * @param <T>       The Type of the enum class
     * @return A set of all fields in the enum class
     * @throws IllegalStateException if the enum set is not present in the cache
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> EnumSet<T> getEnumEntries(@NotNull Class<T> enumClass) {
        checkClass(enumClass);

        EnumSet<T> enumSet = (EnumSet<T>) CACHE.get(enumClass).base;
        if (enumSet != null) {
            return enumSet;
        }

        throw EnumUtil.BASE_NULL;
    }

    @NotNull
    private static <T extends Enum<T>> Entry compute(@NotNull Class<T> enumClass) {
        try {
            return EnumUtil.generateEntry(EnumSet.allOf(enumClass));
        } catch (Throwable ignored) {
        }

        return Entry.EMPTY;
    }

    @NotNull
    private static <T extends Enum<T>> Entry generateEntry(@NotNull EnumSet<T> enums) {
        Entry entry = new Entry(enums);
        for (Enum<T> anEnum : enums) {
            entry.namesToConstant.put(anEnum.name().toLowerCase(), anEnum);
            entry.indexToConstant.put(anEnum.ordinal(), anEnum);
        }

        return entry;
    }

    private static <T extends Enum<T>> void checkClass(@NotNull Class<T> enumClass) {
        if (CACHE.containsKey(enumClass)) {
            Entry entry = CACHE.get(enumClass);
            if (entry.base == null || entry.indexToConstant == null || entry.namesToConstant == null) {
                CACHE.remove(enumClass); // may caused because of gc, remove and refill
                CACHE.put(enumClass, EnumUtil.compute(enumClass));
            }
        } else {
            CACHE.put(enumClass, EnumUtil.compute(enumClass));
        }
    }

    private static final class Entry {

        protected static final Entry EMPTY = new Entry();

        private final Map<String, Enum<?>> namesToConstant;
        private final Map<Integer, Enum<?>> indexToConstant;
        private final EnumSet<?> base;

        private Entry() {
            this.namesToConstant = new ConcurrentHashMap<>();
            this.indexToConstant = new ConcurrentHashMap<>();
            this.base = null;
        }

        private Entry(@NotNull EnumSet<?> set) {
            this.namesToConstant = new ConcurrentHashMap<>(set.size());
            this.indexToConstant = new ConcurrentHashMap<>(set.size());
            this.base = set;
        }
    }
}
