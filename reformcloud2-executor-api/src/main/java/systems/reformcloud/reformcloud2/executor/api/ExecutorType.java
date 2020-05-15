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
package systems.reformcloud.reformcloud2.executor.api;

import java.util.HashMap;
import java.util.Map;

public enum ExecutorType {

    CONTROLLER(1, true),

    CLIENT(2, true),

    NODE(4, true),

    API(3, true),

    UNKNOWN(-1, false);

    private static final Map<Integer, ExecutorType> BY_ID = new HashMap<>();

    static {
        for (ExecutorType executorType : values()) {
            BY_ID.put(executorType.getId(), executorType);
        }
    }

    private final int id;
    private final boolean supported;

    ExecutorType(int id, boolean supported) {
        this.id = id;
        this.supported = supported;
    }

    /* ============================== */

    public static ExecutorType getByID(int id) {
        return BY_ID.getOrDefault(id, UNKNOWN);
    }

    public int getId() {
        return id;
    }

    public boolean isSupported() {
        return supported;
    }

}
