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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.dependency.Dependency;
import systems.reformcloud.dependency.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class can create proxied {@link Dependency} annotations which were parsed from a file. As these methods
 * may change from release to release this class is marked as internal.
 *
 * @author derklaro
 * @since 7. October 2020
 */
@ApiStatus.Internal
public final class DependencyFileLoader {

  private DependencyFileLoader() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  @UnmodifiableView
  public static Collection<Dependency> collectDependenciesFromFile(@Nullable InputStream inputStream) {
    if (inputStream == null) {
      return Collections.emptyList();
    }

    final List<String> fileLines;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      fileLines = reader.lines().collect(Collectors.toList());
    } catch (IOException exception) {
      exception.printStackTrace();
      return Collections.emptyList();
    }

    Collection<Dependency> dependencies = new ArrayList<>();
    for (String fileLine : fileLines) {
      if (fileLine.trim().isEmpty() || fileLine.trim().equals(System.lineSeparator())) {
        // The line is empty or only the line separator so we can skip it
        continue;
      }

      // A dependency file should be in the format repo-id=>repo-url group-id:artifact-id:version
      // So we split up the file line to detect the two parts of a file line and assemble them later together.
      final String[] parts = fileLine.split(" ");
      // Now we can detect the repository parts
      final String[] repositoryParts = parts[0].split("=>");
      // And the artifact information parts
      final String[] artifactInformationParts = parts[1].split(":");

      // Now we create a proxied dependency annotation and let it fail if the user created an invalid file
      dependencies.add(createProxied(repositoryParts, artifactInformationParts));
    }

    return dependencies;
  }

  @NotNull
  private static Dependency createProxied(@NonNls String[] repositoryParts, @NonNls String[] artifactInformationParts) {
    Map<String, Object> repoInformation = new ConcurrentHashMap<>();
    // insert the default values as we only support a few options via file
    repoInformation.put("name", "");
    // proxy the repository information
    repoInformation.put("id", repositoryParts[0]);
    repoInformation.put("url", repositoryParts[1]);
    // now create the repository annotation
    Repository repo = (Repository) Proxy.newProxyInstance(
      Repository.class.getClassLoader(),
      new Class[]{Repository.class},
      new AnnotationInvocationHandler(Repository.class, repoInformation)
    );

    Map<String, Object> dependencyInformation = new ConcurrentHashMap<>();
    // insert the default values as we only support a few options via file
    dependencyInformation.put("optional", false);
    dependencyInformation.put("type", "jar");
    dependencyInformation.put("systemPath", "");
    // now append the repository which we created before
    dependencyInformation.put("repository", repo);
    // proxy the dependency information
    dependencyInformation.put("groupId", artifactInformationParts[0]);
    dependencyInformation.put("artifactId", artifactInformationParts[1]);
    dependencyInformation.put("version", artifactInformationParts[2]);
    // now build the dependency annotation
    return (Dependency) Proxy.newProxyInstance(
      Dependency.class.getClassLoader(),
      new Class[]{Dependency.class},
      new AnnotationInvocationHandler(Dependency.class, dependencyInformation)
    );
  }

  private static final class AnnotationInvocationHandler implements InvocationHandler {

    private final Class<? extends Annotation> type;
    private final Map<String, Object> information;
    // Lazy initialized
    private String toString;
    private Integer hashCode;

    private AnnotationInvocationHandler(Class<? extends Annotation> type, Map<String, Object> information) {
      this.type = type;
      this.information = information;
    }

    private static int memberValueHashCode(@NotNull Object value) {
      Class<?> type = value.getClass();
      if (!type.isArray()) {
        return value.hashCode();
      }

      if (type == byte[].class) {
        return Arrays.hashCode((byte[]) value);
      } else if (type == char[].class) {
        return Arrays.hashCode((char[]) value);
      } else if (type == double[].class) {
        return Arrays.hashCode((double[]) value);
      } else if (type == float[].class) {
        return Arrays.hashCode((float[]) value);
      } else if (type == int[].class) {
        return Arrays.hashCode((int[]) value);
      } else if (type == long[].class) {
        return Arrays.hashCode((long[]) value);
      } else if (type == short[].class) {
        return Arrays.hashCode((short[]) value);
      } else if (type == boolean[].class) {
        return Arrays.hashCode((boolean[]) value);
      }

      return Arrays.hashCode((Object[]) value);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      if (method.getParameterCount() > 1) {
        throw new IllegalStateException("Too many parameters for annotation method call");
      }

      switch (method.getName()) {
        case "toString":
          return this.informationToString();
        case "hashCode":
          return this.informationHashCode();
        case "annotationType":
          return this.type;
        case "equals": {
          if (method.getParameterCount() == 1) {
            // for some internal reasons we don't need a deep equals so we just
            // check if the annotation is the same stored in memory
            return proxy == args[0];
          }
        }
        default: {
          Object result = this.information.get(method.getName());
          if (result == null) {
            throw new IllegalStateException("Unable to get return value of annotation method " + method.getName());
          }

          return result;
        }
      }
    }

    private String informationToString() {
      if (this.toString != null) {
        return this.toString;
      }

      StringBuilder stringBuilder = new StringBuilder().append(this.type.getSimpleName()).append("{");
      for (Map.Entry<String, Object> stringObjectEntry : this.information.entrySet()) {
        stringBuilder.append(stringObjectEntry.getKey()).append("=").append(stringObjectEntry.getValue().toString()).append(", ");
      }

      if (!this.information.isEmpty()) {
        stringBuilder.substring(stringBuilder.length() - 2);
      }

      return this.toString = stringBuilder.append("}").toString();
    }

    private int informationHashCode() {
      if (this.hashCode != null) {
        return this.hashCode;
      }

      int result = 0;
      for (Map.Entry<String, Object> entry : this.information.entrySet()) {
        result += (127 * entry.getKey().hashCode()) ^ memberValueHashCode(entry.getValue());
      }

      return this.hashCode = result;
    }
  }
}
