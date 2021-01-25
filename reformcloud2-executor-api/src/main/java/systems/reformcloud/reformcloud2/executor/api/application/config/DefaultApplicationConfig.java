/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.executor.api.application.config;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.jar.JarEntry;

final class DefaultApplicationConfig implements ApplicationConfig {

  private final String name;
  private final String main;
  private final Path appFile;
  private final String author;
  private final String website;
  private final String version;
  private final JarEntry descFile;
  private final String description;
  private final String implementedVersion;

  public DefaultApplicationConfig(String name, String main, Path appFile, String author, String website, String version,
                                  JarEntry descFile, String description, String implementedVersion) {
    this.name = name;
    this.main = main;
    this.appFile = appFile;
    this.author = author;
    this.website = website;
    this.version = version;
    this.descFile = descFile;
    this.description = description;
    this.implementedVersion = implementedVersion;
  }

  @Override
  public @NotNull String getVersion() {
    return this.version;
  }

  @Override
  public @NotNull String getAuthor() {
    return this.author;
  }

  @Override
  public @NotNull String getMainClassName() {
    return this.main;
  }

  @Override
  public @NotNull String getDescription() {
    return this.description;
  }

  @Override
  public @NotNull String getWebsite() {
    return this.website;
  }

  @Override
  public @NotNull String getImplementedVersion() {
    return this.implementedVersion;
  }

  @Override
  public @NotNull Path getApplicationPath() {
    return this.appFile;
  }

  @Override
  public @NotNull JarEntry getApplicationConfigJarEntry() {
    return this.descFile;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }
}
