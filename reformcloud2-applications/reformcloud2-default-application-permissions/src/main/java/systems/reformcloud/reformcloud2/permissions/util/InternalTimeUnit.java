/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.permissions.util;

import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class InternalTimeUnit {

    private InternalTimeUnit() {
        throw new UnsupportedOperationException();
    }

    public static long convert(@Nullable TimeUnit timeUnit, long time) {
        return timeUnit != null ? timeUnit.toMillis(time) : convertMonth(time);
    }

    private static long convertMonth(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MONTH, toInt(time));
        return calendar.getTimeInMillis();
    }

    private static int toInt(long l) {
        if (l > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return l < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) l;
    }
}
