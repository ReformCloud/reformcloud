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
package systems.reformcloud.reformcloud2.file;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.dependency.repo.Repository;
import systems.reformcloud.reformcloud2.executor.api.dependency.util.DependencyParser;
import systems.reformcloud.reformcloud2.executor.api.provider.DatabaseProvider;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FileDatabase extends Application {

    private DatabaseProvider before;

    @Override
    public void onLoad() {
        DependencyParser.getAllDependencies("dependencies.txt", this.prepareRepositoryGetter(), FileDatabase.class.getClassLoader()).forEach(e -> {
            URL dependencyURL = FileDatabase.this.getDependencyLoader().loadDependency(e);
            Conditions.nonNull(dependencyURL, "Dependency load for " + e.getArtifactID() + " failed");
            FileDatabase.this.getDependencyLoader().addDependency(dependencyURL);
        });

        this.before = ExecutorAPI.getInstance().getDatabaseProvider();
        ExecutorAPI.getInstance().getServiceRegistry().setProvider(DatabaseProvider.class, new FileDatabaseProvider(), false, true);
    }

    @Override
    public void onDisable() {
        ExecutorAPI.getInstance().getServiceRegistry().setProvider(DatabaseProvider.class, this.before, false, true);
    }

    @NotNull
    private Map<String, Repository> prepareRepositoryGetter() {
        Map<String, Repository> out = new HashMap<>();
        out.put("com.github.derklaro.project-deer:project-deer-executor", DefaultRepositories.JITPACK);
        out.put("com.github.derklaro.project-deer:project-deer-api", DefaultRepositories.JITPACK);
        return out;
    }
}
