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
import systems.reformcloud.reformcloud2.executor.api.utility.MoreObjects;

import java.nio.file.Path;
import java.util.jar.JarEntry;

public final class ApplicationConfigBuilder {

  private String name;
  private String main;
  private String author;
  private String version;
  private Path appFile;
  private JarEntry descFile;
  private String implementedVersion = "3.0.0-SNAPSHOT";
  private String website = "https://reformcloud.systems";
  private String description = "A reformcloud application";

  ApplicationConfigBuilder() {
  }

  @NotNull
  public ApplicationConfigBuilder name(@NotNull String name) {
    this.name = name;
    return this;
  }

  @NotNull
  public ApplicationConfigBuilder main(@NotNull String main) {
    this.main = main;
    return this;
  }

  @NotNull
  public ApplicationConfigBuilder author(String author) {
    this.author = author;
    return this;
  }

  @NotNull
  public ApplicationConfigBuilder version(String version) {
    this.version = version;
    return this;
  }

  @NotNull
  public ApplicationConfigBuilder appFile(Path appFile) {
    this.appFile = appFile;
    return this;
  }

  @NotNull
  public ApplicationConfigBuilder descFile(JarEntry descFile) {
    this.descFile = descFile;
    return this;
  }

  @NotNull
  public ApplicationConfigBuilder implementedVersion(String implementedVersion) {
    this.implementedVersion = MoreObjects.firstNotNull(implementedVersion, this.implementedVersion);
    return this;
  }

  @NotNull
  public ApplicationConfigBuilder website(String website) {
    this.website = MoreObjects.firstNotNull(website, this.website);
    return this;
  }

  @NotNull
  public ApplicationConfigBuilder description(String description) {
    this.description = MoreObjects.firstNotNull(description, this.description);
    return this;
  }

  @NotNull
  public ApplicationConfig build() {
    MoreObjects.ensureAllNotNull(this.name, this.main, this.author, this.version, this.appFile, this.descFile);
    return new DefaultApplicationConfig(this.name, this.main, this.appFile, this.author, this.website,
      this.version, this.descFile, this.description, this.implementedVersion);
  }
}
