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

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.*;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.builder.ApplicationConfigBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.application.event.ApplicationDisableEvent;
import systems.reformcloud.reformcloud2.executor.api.common.application.event.ApplicationLoadEvent;
import systems.reformcloud.reformcloud2.executor.api.common.application.event.ApplicationLoaderDetectedApplicationEvent;
import systems.reformcloud.reformcloud2.executor.api.common.application.loader.AppClassLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.unsafe.ApplicationUnsafe;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class DefaultApplicationLoader implements ApplicationLoader {

    private static final Path APPLICATION_DIRECTORY = Paths.get("reformcloud", "applications");

    private static final DependencyLoader APP_LOADER = new DefaultDependencyLoader();

    private final Map<String, Application> loadedApplications = new ConcurrentHashMap<>();

    private final Collection<ApplicationConfig> toLoad = new CopyOnWriteArrayList<>();

    @Override
    public void detectApplications() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(APPLICATION_DIRECTORY, path -> path.toString().endsWith(".jar"))) {
            for (Path path : stream) {
                if (this.loadedApplications.values().stream().anyMatch(e -> e.getApplication().getApplicationConfig().getApplicationFile().toPath().toString().equals(path.toString()))) {
                    continue;
                }

                this.detectApplication(path);
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void installApplications() {
        this.toLoad.forEach(this::installApplication);
        this.toLoad.clear();
    }

    @Override
    public void loadApplications() {
        for (Application value : this.loadedApplications.values()) {
            ApplicationLoadEvent event = new ApplicationLoadEvent(value.getApplication());
            ExecutorAPI.getInstance().getEventManager().callEvent(event);
            if (event.isCancelled()) {
                continue;
            }

            value.onLoad();
            value.getApplication().setApplicationStatus(ApplicationStatus.LOADED);
            System.out.println(LanguageManager.get("successfully-loaded-app", value.getApplication().getName()));
        }
    }

    @Override
    public void enableApplications() {
        for (Application value : this.loadedApplications.values()) {
            value.onEnable();
            value.getApplication().setApplicationStatus(ApplicationStatus.ENABLED);
            System.out.println(LanguageManager.get("successfully-enabled-app", value.getApplication().getName()));
        }
    }

    @Override
    public void disableApplications() {
        this.loadedApplications.values().forEach(this::disableApplication);
    }

    @Override
    public void fetchAllUpdates() {
        this.loadedApplications.values().forEach(this::handleUpdate);
    }

    @Override
    public void fetchUpdates(@NotNull String application) {
        Streams.filterToReference(this.loadedApplications.values(), e -> e.getApplication().getName().equals(application)).ifPresent(this::handleUpdate);
    }

    @Override
    public boolean doSpecificApplicationInstall(@NotNull InstallableApplication application) {
        DownloadHelper.downloadAndDisconnect(application.getDownloadUrl(), "reformcloud/applications/" + application.getName());
        Path path = Paths.get("reformcloud/applications/" + application.getName());

        if (Files.exists(path)) {
            ApplicationConfig config = this.detectApplication(path);
            if (config == null) {
                return false;
            }

            Application app = this.installApplication(config);
            if (app != null) {
                app.onLoad();
                app.onEnable();
                app.getApplication().setApplicationStatus(ApplicationStatus.ENABLED);
            }

            return app != null;
        }

        return false;
    }

    @Override
    public boolean doSpecificApplicationUninstall(@NotNull LoadedApplication loadedApplication) {
        return Streams.filterToReference(
                this.loadedApplications.values(),
                e -> e.getApplication().getName().equals(loadedApplication.getName())
        ).ifPresent(this::disableApplication).isPresent();
    }

    @NotNull
    @Override
    public Optional<LoadedApplication> getApplication(@NotNull String name) {
        return Optional.ofNullable(this.loadedApplications.get(name)).map(Application::getApplication);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<LoadedApplication> getApplications() {
        return Collections.unmodifiableCollection(this.loadedApplications.values().stream().map(Application::getApplication).collect(Collectors.toList()));
    }

    @Nullable
    private ApplicationConfig detectApplication(@NotNull Path path) {
        try (JarFile jarFile = new JarFile(path.toFile())) {
            JarEntry applicationConfigEntry = jarFile.getJarEntry("application.json");
            if (applicationConfigEntry == null) {
                applicationConfigEntry = jarFile.getJarEntry("app.json");

                if (applicationConfigEntry == null) {
                    System.err.println("An application has to contain a application.json or app.json @ " + path.toString());
                    return null;
                }
            }

            try (InputStream inputStream = jarFile.getInputStream(applicationConfigEntry)) {
                JsonConfiguration jsonConfiguration = new JsonConfiguration(inputStream);

                ApplicationConfig applicationConfig = new ApplicationConfigBuilder(
                        jsonConfiguration.getString("name"),
                        jsonConfiguration.getString("main"),
                        jsonConfiguration.getString("author"),
                        jsonConfiguration.getString("version"),
                        path.toFile(),
                        applicationConfigEntry
                )
                        .withDependencies(jsonConfiguration.getOrDefault("dependencies", new TypeToken<List<Dependency>>() {
                        }.getType(), new ArrayList<>()))
                        .withDescription(jsonConfiguration.getOrDefault("description", (String) null))
                        .withWebsite(jsonConfiguration.getOrDefault("website", (String) null))
                        .withImplementedVersion(jsonConfiguration.getOrDefault("impl-version", (String) null))
                        .create();

                ApplicationUnsafe.checkIfUnsafe(applicationConfig);
                if (this.getApplication(applicationConfig.getName()).isPresent()) {
                    System.err.println("Detected duplicate application " + applicationConfig.getName() + " @ " + path.toString());
                    return null;
                }

                ApplicationLoaderDetectedApplicationEvent event = new ApplicationLoaderDetectedApplicationEvent(applicationConfig);
                ExecutorAPI.getInstance().getEventManager().callEvent(event);
                if (event.isCancelled()) {
                    return null;
                }

                this.toLoad.add(applicationConfig);
                System.out.println(LanguageManager.get("application-detected", applicationConfig.getName(), path.toString()));
                return applicationConfig;
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @Nullable
    private Application installApplication(@NotNull ApplicationConfig applicationConfig) {
        try {
            if (applicationConfig.getDependencies().length != 0) {
                for (Dependency dependency : applicationConfig.getDependencies()) {
                    URL dependencyUrl = APP_LOADER.loadDependency(dependency);
                    if (dependencyUrl == null) {
                        System.err.println("Unable to resolve dependency " + dependency.getArtifactID() + " for app " + applicationConfig.getName());
                        return null;
                    }

                    APP_LOADER.addDependency(dependencyUrl);
                }
            }

            AppClassLoader loader = new AppClassLoader(new URL[]{applicationConfig.getApplicationFile().toURI().toURL()}, Thread.currentThread().getContextClassLoader());
            Class<?> mainClass = loader.loadClass(applicationConfig.getMainClassName());

            Application instance = (Application) mainClass.getDeclaredConstructor().newInstance();
            System.out.println(LanguageManager.get("successfully-pre-installed-app", applicationConfig.getName(), applicationConfig.getAuthor()));

            instance.onInstallable();
            instance.init(new DefaultLoadedApplication(this, applicationConfig, mainClass), loader);
            instance.onInstalled();
            instance.getApplication().setApplicationStatus(ApplicationStatus.INSTALLED);

            if (instance.getUpdateRepository() != null) {
                instance.getUpdateRepository().fetchOrigin();
            }

            this.loadedApplications.put(applicationConfig.getName(), instance);
            System.out.println(LanguageManager.get("successfully-installed-app", applicationConfig.getName()));
            return instance;
        } catch (final ClassNotFoundException exception) {
            System.err.println("Unable to find main class " + applicationConfig.getMainClassName() + " for application " + applicationConfig.getName());
        } catch (final NoSuchMethodException exception) {
            System.err.println("Unable to find NoArgsConstructor in class " + applicationConfig.getMainClassName() + " in application " + applicationConfig.getName());
        } catch (final IOException | IllegalAccessException | InstantiationException | InvocationTargetException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    private void disableApplication(@NotNull Application application) {
        ApplicationDisableEvent event = new ApplicationDisableEvent(application.getApplication());
        ExecutorAPI.getInstance().getEventManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        application.getApplication().setApplicationStatus(ApplicationStatus.PRE_DISABLE);
        application.onPreDisable();
        System.out.println(LanguageManager.get("successfully-pre-disabled-app", application.getApplication().getName()));

        application.getApplication().setApplicationStatus(ApplicationStatus.DISABLED);
        application.onDisable();

        application.getApplication().setApplicationStatus(ApplicationStatus.UNINSTALLING);
        application.onUninstall();

        System.out.println(LanguageManager.get("successfully-uninstalled-app", application.getApplication().getName()));
        application.getApplication().setApplicationStatus(ApplicationStatus.UNINSTALLED);

        this.handleUpdate(application);

        application.unloadAllLanguageFiles();
        this.loadedApplications.remove(application.getApplication().getName());
        application.getAppClassLoader().close();
    }

    private void handleUpdate(@NotNull Application application) {
        // Do not fetch updates on development builds
        if (System.getProperty("reformcloud.runner.specification").equals("SNAPSHOT")) {
            return;
        }

        ApplicationUpdateRepository repository = application.getUpdateRepository();
        if (repository == null || !repository.isNewVersionAvailable() || repository.getUpdate() == null) {
            return;
        }

        SystemHelper.createDirectory(Paths.get("reformcloud/.update/apps"));
        String fileName = application.getApplication().getApplicationConfig().getApplicationFile().getName();
        String[] split = fileName.split("-");
        String name = fileName.replace("-" + split[split.length - 1], "").replace(".jar", "");

        System.out.println(LanguageManager.get(
                "application-download-update",
                application.getApplication().getApplicationConfig().getName(),
                application.getApplication().getApplicationConfig().getVersion(),
                repository.getUpdate().getNewVersion(),
                repository.getName(),
                repository.getUpdate().getDownloadUrl()
        ));

        DownloadHelper.downloadAndDisconnect(
                repository.getUpdate().getDownloadUrl(),
                "reformcloud/.update/apps/" + name + "-" + repository.getUpdate().getNewVersion() + ".jar"
        );
    }
}
