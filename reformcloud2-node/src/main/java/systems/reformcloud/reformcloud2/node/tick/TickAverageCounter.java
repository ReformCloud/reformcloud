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
package systems.reformcloud.reformcloud2.node.tick;

public final class TickAverageCounter {

    private final int size;
    private long time;
    private double total;
    private int index = 0;
    private final double[] samples;
    private final long[] times;

    TickAverageCounter(int size) {
        this.size = size;
        this.time = size * CloudTickWorker.SEC_IN_NANO;
        this.total = CloudTickWorker.TPS * CloudTickWorker.SEC_IN_NANO * size;
        this.samples = new double[size];
        this.times = new long[size];
        for (int i = 0; i < size; i++) {
            this.samples[i] = CloudTickWorker.TPS;
            this.times[i] = CloudTickWorker.SEC_IN_NANO;
        }
    }

    public void add(double x, long t) {
        this.time -= this.times[this.index];
        this.total -= this.samples[this.index] * this.times[this.index];
        this.samples[this.index] = x;
        this.times[this.index] = t;
        this.time += t;
        this.total += x * t;
        if (++this.index == this.size) {
            this.index = 0;
        }
    }

    public double getAverage() {
        return this.total / this.time;
    }
}
