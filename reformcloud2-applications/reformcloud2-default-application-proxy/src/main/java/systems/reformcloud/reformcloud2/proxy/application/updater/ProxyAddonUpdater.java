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
package systems.reformcloud.reformcloud2.proxy.application.updater;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.application.updater.ApplicationRemoteUpdate;
import systems.reformcloud.reformcloud2.executor.api.application.updater.basic.BasicApplicationRemoteUpdate;
import systems.reformcloud.reformcloud2.executor.api.application.updater.basic.DefaultApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.io.DownloadHelper;
import systems.reformcloud.reformcloud2.proxy.application.ProxyApplication;

import java.io.IOException;
import java.util.Properties;

public class ProxyAddonUpdater extends DefaultApplicationUpdateRepository {

    private String newVersion;

    @Override
    public void fetchOrigin() {
        DownloadHelper.openConnection("https://internal.reformcloud.systems/version.properties", inputStream -> {
            try {
                Properties properties = new Properties();
                properties.load(inputStream);

                this.newVersion = properties.getProperty("version");
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public boolean isNewVersionAvailable() {
        return !ProxyApplication.getInstance().getApplication().getApplicationConfig().getVersion().equals(this.newVersion);
    }

    @Nullable
    @Override
    public ApplicationRemoteUpdate getUpdate() {
        if (!this.isNewVersionAvailable()) {
            return null;
        }

        return new BasicApplicationRemoteUpdate(this.newVersion,
                "https://dl.reformcloud.systems/addonsv2/reformcloud2-default-application-proxy-" + this.newVersion + ".jar");
    }
}
