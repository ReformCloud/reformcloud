/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.node.application;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.application.AppClassLoader;
import systems.reformcloud.application.Application;
import systems.reformcloud.application.ApplicationLoader;
import systems.reformcloud.application.ApplicationStatus;
import systems.reformcloud.application.LoadedApplication;
import systems.reformcloud.application.config.ApplicationConfig;
import systems.reformcloud.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.language.TranslationHolder;
import systems.reformcloud.node.event.application.ApplicationDisableEvent;
import systems.reformcloud.node.event.application.ApplicationLoadEvent;
import systems.reformcloud.node.event.application.ApplicationLoaderDetectedApplicationEvent;
import systems.reformcloud.shared.io.DownloadHelper;
import systems.reformcloud.shared.io.IOUtils;
import systems.reformcloud.utility.MoreCollections;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class DefaultApplicationLoader implements ApplicationLoader {

  private static final Path APPLICATION_DIRECTORY = Paths.get(System.getProperty("systems.reformcloud.application-directory", "reformcloud/applications"));

  private final Map<String, Application> loadedApplications = new ConcurrentHashMap<>();
  private final Collection<ApplicationConfig> detectedApplications = new CopyOnWriteArrayList<>();

  @Override
  public void detectApplications() {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(APPLICATION_DIRECTORY, path -> path.toString().endsWith(".jar"))) {
      for (Path path : stream) {
        if (this.loadedApplications.values().stream().anyMatch(e -> e.getApplication().getApplicationConfig().getApplicationPath().toString().equals(path.toString()))) {
          continue;
        }

        this.detectApplication(path);
      }
    } catch (final IOException exception) {
      exception.printStackTrace();
    }

    System.out.println(TranslationHolder.translate("application-loaded-amount", this.detectedApplications.size()));
  }

  @Override
  public void loadApplications() {
    this.detectedApplications.forEach(this::installApplication);
    this.detectedApplications.clear();

    for (Application value : this.loadedApplications.values()) {
      ApplicationLoadEvent event = new ApplicationLoadEvent(value.getApplication());
      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
      if (event.isCancelled()) {
        continue;
      }

      value.onLoad();
      value.getApplication().setApplicationStatus(ApplicationStatus.LOADED);
      System.out.println(TranslationHolder.translate("successfully-loaded-app", value.getApplication().getName()));
    }
  }

  @Override
  public void enableApplications() {
    for (Application value : this.loadedApplications.values()) {
      value.onEnable();
      value.getApplication().setApplicationStatus(ApplicationStatus.ENABLED);
      System.out.println(TranslationHolder.translate("successfully-enabled-app", value.getApplication().getName()));
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
    MoreCollections.findFirst(this.loadedApplications.values(), e -> e.getApplication().getName().equals(application)).ifPresent(this::handleUpdate);
  }

  @Override
  public boolean doSpecificApplicationInstall(@NotNull Path path) {
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
    final Optional<Application> application = MoreCollections.findFirst(
      this.loadedApplications.values(),
      e -> e.getApplication().getName().equals(loadedApplication.getName())
    );
    application.ifPresent(this::disableApplication);
    return application.isPresent();
  }

  @NotNull
  @Override
  public Optional<LoadedApplication> getApplication(@NotNull String name) {
    return Optional.ofNullable(this.loadedApplications.get(name)).map(Application::getApplication);
  }

  @NotNull
  @Override
  public @UnmodifiableView Collection<LoadedApplication> getApplications() {
    return this.loadedApplications.values().stream().map(Application::getApplication).collect(Collectors.toList());
  }

  @Override
  public @NotNull Path getApplicationFolder() {
    return APPLICATION_DIRECTORY;
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
        JsonConfiguration jsonConfiguration = JsonConfiguration.newJsonConfiguration(inputStream);

        ApplicationConfig config = ApplicationConfig.builder()
          .name(jsonConfiguration.getString("name"))
          .main(jsonConfiguration.getString("main"))
          .author(jsonConfiguration.getString("author"))
          .version(jsonConfiguration.getString("version"))
          .appFile(path)
          .descFile(applicationConfigEntry)
          .description(jsonConfiguration.getOrDefault("description", (String) null))
          .website(jsonConfiguration.getOrDefault("website", (String) null))
          .implementedVersion(jsonConfiguration.getOrDefault("impl-version", (String) null))
          .build();

        if (this.getApplication(config.getName()).isPresent()) {
          System.err.println("Detected duplicate application " + config.getName() + " @ " + path.toString());
          return null;
        }

        ApplicationLoaderDetectedApplicationEvent event = new ApplicationLoaderDetectedApplicationEvent(config);
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
        if (event.isCancelled()) {
          return null;
        }

        this.detectedApplications.add(config);
        System.out.println(TranslationHolder.translate("application-detected", config.getName(), path.toString()));
        return config;
      }
    } catch (final IOException exception) {
      exception.printStackTrace();
    }

    return null;
  }

  @Nullable
  private Application installApplication(@NotNull ApplicationConfig applicationConfig) {
    try {
      AppClassLoader loader = new AppClassLoader(new URL[]{applicationConfig.getApplicationPath().toUri().toURL()}, Thread.currentThread().getContextClassLoader());
      Class<?> mainClass = loader.loadClass(applicationConfig.getMainClassName());

      // Load the dependencies which are needed for the application to work
      ExecutorAPI.getInstance().getDependencyLoader().detectAndLoad(mainClass);

      Application instance = (Application) mainClass.getDeclaredConstructor().newInstance();
      System.out.println(TranslationHolder.translate("successfully-pre-installed-app", applicationConfig.getName(), applicationConfig.getAuthor()));

      instance.init(new DefaultLoadedApplication(this, applicationConfig, mainClass), loader);
      if (instance.getUpdateRepository() != null) {
        instance.getUpdateRepository().fetchOrigin();
      }

      this.loadedApplications.put(applicationConfig.getName(), instance);
      System.out.println(TranslationHolder.translate("successfully-installed-app", applicationConfig.getName()));
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
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
    if (event.isCancelled()) {
      return;
    }

    application.getApplication().setApplicationStatus(ApplicationStatus.PRE_DISABLE);
    application.onPreDisable();
    System.out.println(TranslationHolder.translate("successfully-pre-disabled-app", application.getApplication().getName()));

    application.getApplication().setApplicationStatus(ApplicationStatus.DISABLED);
    application.onDisable();

    this.handleUpdate(application);

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

    IOUtils.createDirectory(Paths.get("reformcloud/.update/apps"));
    String fileName = application.getApplication().getApplicationConfig().getApplicationPath().getFileName().toString();

    System.out.println(TranslationHolder.translate(
      "application-download-update",
      application.getApplication().getApplicationConfig().getName(),
      application.getApplication().getApplicationConfig().getVersion(),
      repository.getUpdate().getNewVersion(),
      repository.getName(),
      repository.getUpdate().getDownloadUrl()
    ));

    DownloadHelper.download(repository.getUpdate().getDownloadUrl(), "reformcloud/.update/apps/" + fileName);
  }
}
