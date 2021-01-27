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
package systems.reformcloud.reformcloud2.node.process;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.Version;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.VersionInstaller;
import systems.reformcloud.reformcloud2.executor.api.network.address.NetworkAddress;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.process.configurator.ProcessConfigurator;
import systems.reformcloud.reformcloud2.node.process.configurator.ProcessConfiguratorRegistry;
import systems.reformcloud.reformcloud2.node.template.TemplateBackendManager;
import systems.reformcloud.reformcloud2.node.template.VersionInstallerRegistry;
import systems.reformcloud.reformcloud2.shared.Constants;
import systems.reformcloud.reformcloud2.shared.io.DownloadHelper;
import systems.reformcloud.reformcloud2.shared.io.IOUtils;
import systems.reformcloud.reformcloud2.shared.network.NetworkUtils;
import systems.reformcloud.reformcloud2.shared.network.data.DefaultProtocolBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

final class EnvironmentBuilder {

  private EnvironmentBuilder() {
    throw new UnsupportedOperationException();
  }

  protected static void constructEnvFor(@NotNull DefaultNodeLocalProcessWrapper wrapper, boolean firstStart, @NotNull String connectionKey) {
    if (!installVersion(wrapper)) {
      System.err.println("Unable to install version " + wrapper.getProcessInformation().getPrimaryTemplate().getVersion().getName());
      return;
    }

    NetworkAddress address = wrapper.getProcessInformation().getHost();
    address.setPort(NetworkUtils.checkAndReplacePortIfInUse(address.getPort()));

    if (!wrapper.getProcessInformation().getProcessGroup().createsStaticProcesses() || firstStart) {
      loadTemplateInclusions(wrapper, Inclusion.InclusionLoadType.PRE);
      loadPathInclusions(wrapper, Inclusion.InclusionLoadType.PRE);
      initGlobalTemplateAndCurrentTemplate(wrapper);
    }

    ProcessUtil.loadInclusions(wrapper.getPath(), wrapper.getProcessInformation().getProcessInclusions());
    if (!wrapper.getProcessInformation().getProcessGroup().createsStaticProcesses() || firstStart) {
      loadTemplateInclusions(wrapper, Inclusion.InclusionLoadType.PAST);
      loadPathInclusions(wrapper, Inclusion.InclusionLoadType.PAST);
    }

    if (Files.notExists(Paths.get("reformcloud/files/runner.jar"))) {
      DownloadHelper.download(Constants.RUNNER_DOWNLOAD_URL, "reformcloud/files/runner.jar");
    }

    IOUtils.createDirectory(Paths.get(wrapper.getPath() + "/plugins"));
    IOUtils.doCopy("reformcloud/files/runner.jar", wrapper.getPath().resolve("runner.jar"));
    IOUtils.doOverrideInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/embedded.jar", wrapper.getPath() + "/plugins/executor.jar");

    final NetworkAddress connectHost = NodeExecutor.getInstance().getAnyAddress();
    JsonConfiguration.newJsonConfiguration()
      .add("host", connectHost.getHost())
      .add("port", connectHost.getPort())
      .add("key", connectionKey)
      .write(wrapper.getPath() + "/.reformcloud/config.json");
    writeProcessInformation(wrapper);

    final Optional<ProcessConfigurator> configurator = ProcessConfiguratorRegistry.getConfigurator(wrapper.getProcessInformation().getPrimaryTemplate().getVersion().getConfigurator());
    if (configurator.isPresent()) {
      configurator.get().configure(wrapper);
      if (wrapper.getProcessInformation().getPrimaryTemplate().getVersion().getVersionType().isServer()) {
        createEula(wrapper);
      }
    } else {
      System.err.println("Configurator named \"" + wrapper.getProcessInformation().getPrimaryTemplate().getVersion().getConfigurator()
        + "\" for process " + wrapper.getProcessInformation().getName() + " not found");
    }
  }

  private static void loadTemplateInclusions(@NotNull DefaultNodeLocalProcessWrapper processInformation, @NotNull Inclusion.InclusionLoadType loadType) {
    processInformation.getProcessInformation().getPrimaryTemplate().getTemplateInclusions(loadType).forEach(e -> {
      String[] splitTemplate = e.getKey().split("/");
      if (splitTemplate.length != 2) {
        return;
      }

      TemplateBackendManager.getOrDefault(e.getBackend()).loadTemplate(
        splitTemplate[0],
        splitTemplate[1],
        processInformation.getPath()
      ).awaitUninterruptedly();
    });
  }

  private static void loadPathInclusions(@NotNull DefaultNodeLocalProcessWrapper processInformation, @NotNull Inclusion.InclusionLoadType loadType) {
    processInformation.getProcessInformation().getPrimaryTemplate().getPathInclusions(loadType).forEach(e -> {
      TemplateBackendManager.getOrDefault(e.getBackend()).loadPath(
        e.getKey(),
        processInformation.getPath()
      ).awaitUninterruptedly();
    });
  }

  private static void initGlobalTemplateAndCurrentTemplate(@NotNull DefaultNodeLocalProcessWrapper processInformation) {
    TemplateBackendManager.getOrDefault(processInformation.getProcessInformation().getPrimaryTemplate().getBackend()).loadGlobalTemplates(
      processInformation.getProcessInformation().getProcessGroup(),
      processInformation.getPath()
    ).awaitUninterruptedly();

    TemplateBackendManager.getOrDefault(processInformation.getProcessInformation().getPrimaryTemplate().getBackend()).loadTemplate(
      processInformation.getProcessInformation().getProcessGroup().getName(),
      processInformation.getProcessInformation().getPrimaryTemplate().getName(),
      processInformation.getPath()
    ).awaitUninterruptedly();
  }

  private static boolean installVersion(DefaultNodeLocalProcessWrapper wrapper) {
    Version version = wrapper.getProcessInformation().getPrimaryTemplate().getVersion();
    VersionInstaller installer = VersionInstallerRegistry
      .getInstaller(version.getInstaller())
      .orElseThrow(() -> new RuntimeException("Installer " + version.getInstaller() + " not registered"));
    return installer.installVersion(version);
  }

  private static void writeProcessInformation(DefaultNodeLocalProcessWrapper wrapper) {
    final ByteBuf byteBuf = Unpooled.buffer();
    try (OutputStream outputStream = Files.newOutputStream(wrapper.getPath().resolve(".reformcloud/info"))) {
      final ProtocolBuffer buffer = new DefaultProtocolBuffer(byteBuf);
      buffer.writeObject(wrapper.getProcessInformation());
      buffer.transferTo(outputStream);
    } catch (IOException exception) {
      exception.printStackTrace();
    } finally {
      byteBuf.release();
    }
  }

  private static void createEula(@NotNull DefaultNodeLocalProcessWrapper wrapper) {
    try (InputStream inputStream = EnvironmentBuilder.class.getClassLoader().getResourceAsStream("files/java/bukkit/eula.txt")) {
      Files.copy(Objects.requireNonNull(inputStream), wrapper.getPath().resolve("eula.txt"), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }
}
