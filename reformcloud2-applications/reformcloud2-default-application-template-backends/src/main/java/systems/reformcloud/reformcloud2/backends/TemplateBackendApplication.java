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
package systems.reformcloud.reformcloud2.backends;

import systems.reformcloud.reformcloud2.backends.ftp.FTPTemplateBackend;
import systems.reformcloud.reformcloud2.backends.sftp.SFTPTemplateBackend;
import systems.reformcloud.reformcloud2.backends.url.URLTemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.dependency.util.DependencyParser;

import java.net.URL;
import java.util.HashMap;

public class TemplateBackendApplication extends Application {

    @Override
    public void onLoad() {
        DependencyParser.getAllDependencies("dependencies.txt", new HashMap<>(), TemplateBackendApplication.class.getClassLoader()).forEach(e -> {
            URL dependencyURL = TemplateBackendApplication.this.getDependencyLoader().loadDependency(e);
            Conditions.nonNull(dependencyURL, "Dependency load for " + e.getArtifactID() + " failed");
            TemplateBackendApplication.this.getDependencyLoader().addDependency(dependencyURL);
        });

        FTPTemplateBackend.load(this.getDataFolder().getPath());
        SFTPTemplateBackend.load(this.getDataFolder().getPath());
        URLTemplateBackend.load(this.getDataFolder().getPath());
    }

    @Override
    public void onDisable() {
        URLTemplateBackend.unload();
        FTPTemplateBackend.unload();
        SFTPTemplateBackend.unload();
    }
}
