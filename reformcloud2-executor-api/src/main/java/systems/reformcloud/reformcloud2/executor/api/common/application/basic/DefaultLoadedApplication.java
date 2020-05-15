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
package systems.reformcloud.reformcloud2.executor.api.common.application.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationConfig;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationStatus;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;

public final class DefaultLoadedApplication implements LoadedApplication {

    public DefaultLoadedApplication(ApplicationLoader loader, ApplicationConfig application, Class<?> main) {
        this.loader = loader;
        this.application = application;
        this.main = main;
        this.applicationStatus = ApplicationStatus.INSTALLABLE;
    }

    private final ApplicationLoader loader;

    private final Class<?> main;

    private final ApplicationConfig application;

    private ApplicationStatus applicationStatus;

    @NotNull
    @Override
    public ApplicationLoader getApplicationLoader() {
        return loader;
    }

    @NotNull
    @Override
    public ExecutorAPI api() {
        return ExecutorAPI.getInstance();
    }

    @NotNull
    @Override
    public ApplicationConfig getApplicationConfig() {
        return application;
    }

    @NotNull
    @Override
    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    @Override
    public Class<?> getMainClass() {
        return main;
    }

    @Override
    public void setApplicationStatus(@NotNull ApplicationStatus status) {
        this.applicationStatus = status;
    }
}
