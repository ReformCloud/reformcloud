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
package systems.reformcloud.reformcloud2.shared.random;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of George Marsaglia's XORShift random generator
 * 30% faster and better quality than the built-in java.util.Random further
 * information: http://www.javamex.com/tutorials/random_numbers/xorshift.shtml
 */
public class ThreadLocalFastRandom extends Random {

    private static final ThreadLocal<ThreadLocalFastRandom> RANDOM_THREAD_LOCAL = ThreadLocal.withInitial(ThreadLocalFastRandom::new);
    private final AtomicLong seed;

    protected ThreadLocalFastRandom() {
        this.seed = new AtomicLong(System.nanoTime());
    }

    @NotNull
    public static Random current() {
        return RANDOM_THREAD_LOCAL.get();
    }

    @Override
    protected int next(int bits) {
        long x = this.seed.get();
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        this.seed.set(x);
        x &= ((1L << bits) - 1);

        return (int) x;
    }
}
