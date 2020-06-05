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
package systems.reformcloud.reformcloud2.node;

import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.language.loading.LanguageLoader;

import java.nio.file.Paths;

public final class NodeLauncher {

    public static synchronized void main(String[] args) {
        LanguageLoader.doLoad();
        DependencyLoader.doLoad();

        long startTime = System.currentTimeMillis();

        IOUtils.recreateDirectory(Paths.get("reformcloud/temp"));
        NodeExecutor nodeExecutor = new NodeExecutor();
        nodeExecutor.bootstrap();

        double bootTime = (System.currentTimeMillis() - startTime) / 1000d;
        System.out.println(LanguageManager.get("startup-done", CommonHelper.DECIMAL_FORMAT.format(bootTime)));

        nodeExecutor.getCloudTickWorker().startTick();
    }
}
