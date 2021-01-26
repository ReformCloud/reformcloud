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
package systems.reformcloud.reformcloud2.node.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.console.Console;
import systems.reformcloud.reformcloud2.executor.api.group.setup.GroupSetupVersion;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.Versions;
import systems.reformcloud.reformcloud2.executor.api.language.TranslationHolder;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.setup.DefaultSetup;
import systems.reformcloud.reformcloud2.node.setup.DefaultSetupQuestion;
import systems.reformcloud.reformcloud2.node.setup.Setup;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

final class GroupSetupHelper {

  private GroupSetupHelper() {
    throw new UnsupportedOperationException();
  }

  public static void runSetup() {
    Setup setup = new DefaultSetup();

    boolean doProxySetup = ask(setup, TranslationHolder.translate("node-setup-question-boolean"), TranslationHolder.translate("node-setup-default-install", "Proxy"));
    if (doProxySetup) {
      runSetup(false);
    }

    boolean doServerSetup = ask(setup, TranslationHolder.translate("node-setup-question-boolean"), TranslationHolder.translate("node-setup-default-install", "Server"));
    if (doServerSetup) {
      runSetup(true);
    }
  }

  private static boolean ask(@NotNull Setup setup, @NotNull String invalidAnswer, @NotNull String question) {
    setup.clear();

    AtomicBoolean result = new AtomicBoolean();
    setup.addQuestion(new DefaultSetupQuestion(
      answer -> {
        final Boolean aBoolean = answer.getAsBoolean();
        if (aBoolean != null) {
          result.set(aBoolean);
        }
        return aBoolean != null;
      },
      invalidAnswer,
      question
    ));
    setup.runSetup();
    return result.get();
  }

  private static void runSetup(boolean server) {
    Map<String, GroupSetupVersion> versions = Versions.getKnownVersions()
      .values()
      .stream()
      .filter(version -> version.getVersionType().isServer() == server)
      .map(version -> new VersionedGroupSetupVersion(server ? "Lobbies" : "Proxies", server ? "Lobby" : "Proxy", version))
      .collect(Collectors.toMap(GroupSetupVersion::getName, Function.identity()));

    final Console console = NodeExecutor.getInstance().getConsole();
    console.clearHistory();

    System.out.println(TranslationHolder.translate("general-setup-choose-default-installation"));
    for (GroupSetupVersion version : versions.values()) {
      System.out.println(version.getName());
      console.addHistoryEntry(version.getName());
    }

    String line = NodeExecutor.getInstance().getConsole().readString().getUninterruptedly();
    while (line != null && !line.trim().isEmpty()) {
      final GroupSetupVersion version = versions.get(line.toLowerCase(Locale.ROOT));
      if (version == null) {
        System.out.println(TranslationHolder.translate("general-setup-choose-default-installation-wrong"));
        line = NodeExecutor.getInstance().getConsole().readString().getUninterruptedly();
      } else {
        version.install(
          NodeExecutor.getInstance().getDefaultProcessGroupProvider()::addProcessGroup,
          NodeExecutor.getInstance().getDefaultMainGroupProvider()::addGroup
        );
        break;
      }
    }

    console.clearScreen();
    console.clearHistory();

    System.out.println(TranslationHolder.translate("node-setup-version-installed"));
  }
}
