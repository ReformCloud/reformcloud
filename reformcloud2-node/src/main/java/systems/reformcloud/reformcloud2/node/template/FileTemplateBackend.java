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
package systems.reformcloud.reformcloud2.node.template;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.groups.process.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.groups.template.builder.TemplateBuilder;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.utility.MoreCollections;
import systems.reformcloud.reformcloud2.shared.io.IOUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class FileTemplateBackend implements TemplateBackend {

  public static final String NAME = TemplateBuilder.FILE_BACKEND;

  @Override
  public boolean existsTemplate(@NotNull String group, @NotNull String template) {
    return Files.exists(this.format(group, template));
  }

  @Override
  public void createTemplate(@NotNull String group, @NotNull String template) {
    if (!this.existsTemplate(group, template)) {
      IOUtils.createDirectory(Paths.get("reformcloud/templates", group, template, "plugins"));
    }
  }

  @NotNull
  @Override
  public Task<Void> loadTemplate(@NotNull String group, @NotNull String template, @NotNull Path target) {
    if (!this.existsTemplate(group, template)) {
      this.createTemplate(group, template);
      return Task.completedTask(null);
    }

    IOUtils.copyDirectory(this.format(group, template), target);
    return Task.completedTask(null);
  }

  @NotNull
  @Override
  public Task<Void> loadGlobalTemplates(@NotNull ProcessGroup group, @NotNull Path target) {
    MoreCollections.allOf(group.getTemplates(), Template::isGlobal).forEach(e -> this.loadTemplate(group.getName(), e.getName(), target));
    return Task.completedTask(null);
  }

  @NotNull
  @Override
  public Task<Void> loadPath(@NotNull String path, @NotNull Path target) {
    Path localPath = Paths.get(path);
    if (Files.notExists(localPath)) {
      IOUtils.createDirectory(localPath);
      return Task.completedTask(null);
    }

    if (Files.isDirectory(localPath)) {
      IOUtils.copyDirectory(localPath, target);
    }

    return Task.completedTask(null);
  }

  @Override
  public void deployTemplate(@NotNull String group, @NotNull String template, @NotNull Path current, @NotNull Collection<String> collection) {
    if (this.existsTemplate(group, template)) {
      IOUtils.copyDirectory(current, this.format(group, template), collection);
    }
  }

  @Override
  public void deleteTemplate(@NotNull String group, @NotNull String template) {
    if (!this.existsTemplate(group, template)) {
      return;
    }

    IOUtils.deleteDirectory(this.format(group, template));
  }

  @NotNull
  @Override
  public String getName() {
    return NAME;
  }

  private Path format(String group, String template) {
    return Paths.get("reformcloud/templates/" + group + "/" + template);
  }
}
