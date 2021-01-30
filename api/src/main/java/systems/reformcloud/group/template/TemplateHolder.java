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
package systems.reformcloud.group.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A holder for {@link Template}s.
 */
public interface TemplateHolder {

  /**
   * Get all templates known to this holder.
   *
   * @return All templates known to this holder.
   */
  @NotNull
  @UnmodifiableView
  Collection<Template> getTemplates();

  /**
   * Sets all templates which are known to this holder.
   *
   * @param templates The templates which are known to this holder.
   */
  void setTemplates(@NotNull List<Template> templates);

  /**
   * Get a specific template by it's name.
   *
   * @param name The name of the template to get.
   * @return The template with the given {@code name}.
   */
  @NotNull
  Optional<Template> getTemplate(@NotNull String name);

  /**
   * Adds the specified {@code template} to this holder.
   *
   * @param template The template to add.
   */
  void addTemplate(@NotNull Template template);

  /**
   * Removes the specified {@code template} from this holder.
   *
   * @param template The template to remove.
   */
  void removeTemplate(@NotNull Template template);

  /**
   * Removed a specific template by the given {@code name} from this holder.
   *
   * @param name The name of the template to remove.
   */
  void removeTemplate(@NotNull String name);

  /**
   * Gets if a template with the given {@code name} is registered in this holder.
   *
   * @param name The name of the template.
   * @return {@code true} if the template is registered in this holder, else {@code false}.
   */
  boolean isTemplatePresent(@NotNull String name);

  /**
   * Removes all templates from this holder.
   */
  void removeAllTemplates();
}
