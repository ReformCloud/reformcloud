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
package systems.reformcloud.shared.dependency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import systems.reformcloud.dependency.Dependency;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

class DependencyFileLoaderTest {

  @Test
  void testCollectDependenciesFromFile() {
    String fileLines = "central=>https://repo1.maven.org/ org.jetbrains.annotations:annotations:20.1.0\n"
      + " \n"
      + "cloud=>https://repo.reformcloud.systems systems.reformcloud:shared:2.1.0\n"
      + "\n";
    try (InputStream inputStream = new ByteArrayInputStream(fileLines.getBytes(StandardCharsets.UTF_8))) {
      Collection<Dependency> dependencies = DependencyFileLoader.collectDependenciesFromFile(inputStream);
      Assertions.assertEquals(2, dependencies.size());

      // Check if the line 'central=>https://repo1.maven.org/ org.jetbrains.annotations:annotations:20.1.0' was parsed correctly
      Assertions.assertTrue(dependencies.stream().anyMatch(
        // check the dependency information
        d -> d.groupId().equals("org.jetbrains.annotations") && d.artifactId().equals("annotations") && d.version().equals("20.1.0")
          // Checks the repository information
          && d.repository().id().equals("central") && d.repository().url().equals("https://repo1.maven.org/")
      ));
      // Check if the line 'cloud=>https://repo.reformcloud.systems systems.reformcloud:shared:2.1.0' was parsed correctly
      Assertions.assertTrue(dependencies.stream().anyMatch(
        // check the dependency information
        d -> d.groupId().equals("systems.reformcloud") && d.artifactId().equals("shared") && d.version().equals("2.1.0")
          // Checks the repository information
          && d.repository().id().equals("cloud") && d.repository().url().equals("https://repo.reformcloud.systems")
      ));
    } catch (IOException exception) {
      Assertions.fail(exception);
    }
  }
}
