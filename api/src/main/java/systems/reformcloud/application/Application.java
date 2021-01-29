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
package systems.reformcloud.application;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.application.updater.ApplicationUpdateRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

  private Path dataDirectory;
  private LoadedApplication application;
  private ExecutorService executorService;
  private AppClassLoader appClassLoader;

  public final void init(@NotNull LoadedApplication application, AppClassLoader loader) {
    this.application = application;
    this.executorService = Executors.newCachedThreadPool();
    this.appClassLoader = loader;

    this.dataDirectory = application.getApplicationLoader().getApplicationFolder().resolve(application.getName());
    if (Files.notExists(this.dataDirectory)) {
      try {
        Files.createDirectories(this.dataDirectory);
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    }
  }

  public void onLoad() {
  }

  public void onEnable() {
  }

  public void onPreDisable() {
  }

  public void onDisable() {
  }

  @Nullable
  public ApplicationUpdateRepository getUpdateRepository() {
    return null;
  }

  @NotNull
  public Path getDataDirectory() {
    return this.dataDirectory;
  }

  @Nullable
  public final InputStream getResourceAsStream(String name) {
    return this.getClass().getClassLoader().getResourceAsStream(name);
  }

  public final AppClassLoader getAppClassLoader() {
    return this.appClassLoader;
  }

  @NotNull
  public final LoadedApplication getApplication() {
    return this.application;
  }

  public void log(String log) {
    System.out.println(log);
  }

  @NotNull
  public final ExecutorService getExecutorService() {
    return this.executorService;
  }
}
