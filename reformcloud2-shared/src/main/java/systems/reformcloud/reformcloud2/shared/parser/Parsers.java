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
package systems.reformcloud.reformcloud2.shared.parser;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.parse.Parser;
import systems.reformcloud.reformcloud2.shared.collect.Function1E;

import java.util.UUID;

@ApiStatus.Internal
public final class Parsers {

    public static final Parser<String, Long> LONG = numberParser(Long::parseLong);
    public static final Parser<String, Float> FLOAT = numberParser(Float::parseFloat);
    public static final Parser<String, Short> SHORT = numberParser(Short::parseShort);
    public static final Parser<String, Integer> INT = numberParser(Integer::parseInt);
    public static final Parser<String, UUID> UNIQUE_ID = generalParser(UUID::fromString);
    public static final Parser<String, Double> DOUBLE = numberParser(Double::parseDouble);
    public static final Parser<String, Boolean> BOOLEAN = s -> {
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")) {
            return s.equalsIgnoreCase("true");
        } else {
            return null;
        }
    };
    public static final Parser<Long, Integer> LONG_TO_INT = l -> l > Integer.MAX_VALUE ? Integer.MAX_VALUE : l < Integer.MIN_VALUE ? Integer.MIN_VALUE : l.intValue();
    public static final Parser<Throwable, String> EXCEPTION_FORMAT = throwable -> {
        StackTraceElement[] trace = throwable.getStackTrace();
        return throwable.getClass().getSimpleName() + " : " + throwable.getMessage() + (trace.length > 0
            ? " @ " + trace[0].getClassName() + ":" + trace[0].getLineNumber() : ""
        );
    };

    private Parsers() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    private static <T> Parser<String, T> numberParser(@NotNull Function1E<String, T, NumberFormatException> parser) {
        return s -> {
            try {
                return parser.apply(s);
            } catch (NumberFormatException exception) {
                return null;
            }
        };
    }

    @NotNull
    private static <T> Parser<String, T> generalParser(@NotNull Function1E<String, T, Throwable> parser) {
        return s -> {
            try {
                return parser.apply(s);
            } catch (Throwable exception) {
                return null;
            }
        };
    }
}
