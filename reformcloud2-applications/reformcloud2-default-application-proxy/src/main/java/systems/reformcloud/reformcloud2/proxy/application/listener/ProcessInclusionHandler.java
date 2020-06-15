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
package systems.reformcloud.reformcloud2.proxy.application.listener;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.node.event.process.LocalProcessPrePrepareEvent;
import systems.reformcloud.reformcloud2.proxy.application.ProxyApplication;

public final class ProcessInclusionHandler {

    @Listener
    public void handle(final @NotNull LocalProcessPrePrepareEvent event) {
        this.includeSelfFile(event.getProcessInformation());
    }

    private void includeSelfFile(@NotNull ProcessInformation processInformation) {
        if (processInformation.getProcessDetail().getTemplate().getVersion().isServer()) {
            return;
        }

        processInformation.getPreInclusions().add(new ProcessInclusion(
                "https://dl.reformcloud.systems/addonsv2/reformcloud2-default-application-proxy-"
                        + ProxyApplication.getInstance().getApplication().getApplicationConfig().getVersion() + ".jar",
                "plugins/proxy-" + ProxyApplication.getInstance().getApplication().getApplicationConfig().getVersion() + ".jar"
        ));
    }
}
