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
package systems.reformcloud.reformcloud2.executor.controller.config;

import com.google.gson.reflect.TypeToken;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public final class ControllerConfig {

    static final TypeToken<ControllerConfig> TYPE = new TypeToken<ControllerConfig>() {
    };

    static final Path PATH = Paths.get("reformcloud/config.json");
    private final int maxProcesses;
    private final List<Map<String, Integer>> networkListener;
    private final List<Map<String, Integer>> httpNetworkListener;

    ControllerConfig(int maxProcesses, List<Map<String, Integer>> networkListener,
                     List<Map<String, Integer>> httpNetworkListener) {
        this.maxProcesses = maxProcesses;
        this.networkListener = networkListener;
        this.httpNetworkListener = httpNetworkListener;
    }

    public int getMaxProcesses() {
        return this.maxProcesses;
    }

    public List<Map<String, Integer>> getNetworkListener() {
        return this.networkListener;
    }

    public List<Map<String, Integer>> getHttpNetworkListener() {
        return this.httpNetworkListener;
    }
}
