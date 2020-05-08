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
package systems.reformcloud.reformcloud2.executor.api.common.application;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;

import java.util.List;

/**
 * Represents a loader for applications
 */
public interface ApplicationLoader {

    /**
     * Detects all applications
     */
    void detectApplications();

    /**
     * Installs all applications
     */
    void installApplications();

    /**
     * Loads all applications
     */
    void loadApplications();

    /**
     * Enables all applications
     */
    void enableApplications();

    /**
     * Disables all applications
     */
    void disableApplications();

    /**
     * Fetches all updates for all applications &amp; downloads them
     */
    void fetchAllUpdates();

    /**
     * Fetches the updates for a specific addon
     *
     * @param application The name of the application which should get checked
     */
    void fetchUpdates(@NotNull String application);

    /**
     * Installs an specific application
     *
     * @param application The application which should get installed
     * @return If the cloud can find the application and install it {@code true} else {@code false}
     */
    boolean doSpecificApplicationInstall(@NotNull InstallableApplication application);

    /**
     * Unloads a specific application
     *
     * @param loadedApplication The application which should get unloaded
     * @return If the application was loaded and got unloaded
     */
    boolean doSpecificApplicationUninstall(@NotNull LoadedApplication loadedApplication);

    /**
     * Finds the {@link LoadedApplication} by the name and unloads it
     *
     * @param application The name oof the application which should get unloaded
     * @return If the application was loaded and got unloaded
     * @see #getApplication(String)
     * @see #doSpecificApplicationUninstall(LoadedApplication)
     */
    boolean doSpecificApplicationUninstall(@NotNull String application);

    /**
     * Get a specific application
     *
     * @param name The name of the application
     * @return The loaded application or {@code null} if the application is unknown
     */
    @Nullable
    LoadedApplication getApplication(@NotNull String name);

    /**
     * The name of a loaded application
     *
     * @param loadedApplication The application from which the name is needed
     * @return The name of the application
     * @see LoadedApplication#getName()
     */
    @NotNull
    String getApplicationName(@NotNull LoadedApplication loadedApplication);

    /**
     * @return All currently loaded applications in the runtime
     */
    @NotNull
    List<LoadedApplication> getApplications();

    /**
     * Registers an {@link ApplicationHandler}
     *
     * @param applicationHandler The handler which should get registered
     */
    void addApplicationHandler(@NotNull ApplicationHandler applicationHandler);

    @Nullable
    Application getInternalApplication(@NotNull String name);
}
