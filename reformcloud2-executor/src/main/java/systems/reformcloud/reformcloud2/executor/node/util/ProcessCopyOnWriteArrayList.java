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
package systems.reformcloud.reformcloud2.executor.node.util;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public final class ProcessCopyOnWriteArrayList extends CopyOnWriteArrayList<ProcessInformation> {

    private final Object lock = new Object();

    @Override
    public boolean addAll(Collection<? extends ProcessInformation> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean add(ProcessInformation c) {
        this.stream()
                .filter(e -> e.getProcessDetail().getProcessUniqueID().equals(c.getProcessDetail().getProcessUniqueID()))
                .forEach(this::remove);
        return super.add(c);
    }

    @Override
    public void add(int index, ProcessInformation element) {
        this.add(element);
    }

    @Override
    public boolean addAll(int index, Collection<? extends ProcessInformation> c) {
        this.addAll(c);
        return true;
    }

    @Override
    public Stream<ProcessInformation> stream() {
        synchronized (lock) {
            return super.stream();
        }
    }

    @Override
    public Stream<ProcessInformation> parallelStream() {
        synchronized (lock) {
            return super.parallelStream();
        }
    }
}
