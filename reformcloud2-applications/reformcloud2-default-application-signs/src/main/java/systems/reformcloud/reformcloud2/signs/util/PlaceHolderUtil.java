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
package systems.reformcloud.reformcloud2.signs.util;

import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlaceHolderUtil {

  private static final Pattern PATTERN = Pattern.compile(".*?(%sign_layout_place_holder_(\\w+)%).*?", Pattern.CASE_INSENSITIVE);

  private PlaceHolderUtil() {
    throw new UnsupportedOperationException();
  }

  public static <T> T format(String line, String group, ProcessInformation processInformation, Function<String, T> colorize) {
    line = line.replace("%group%", group);
    line = line.replace("%name%", processInformation.getName());
    line = line.replace("%display%", processInformation.getId().getDisplayName());
    line = line.replace("%parent%", processInformation.getId().getNodeName());
    line = line.replace("%id%", Integer.toString(processInformation.getId().getId()));
    line = line.replace("%uniqueid%", processInformation.getId().getUniqueId().toString());
    line = line.replace("%state%", processInformation.getCurrentState().name());
    line = line.replace("%connected%", Boolean.toString(processInformation.getCurrentState().isOnline()));
    line = line.replace("%template%", processInformation.getPrimaryTemplate().getName());
    line = line.replace("%online%", Integer.toString(processInformation.getOnlineCount()));
    line = line.replace("%max%", Integer.toString(processInformation.getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers()));
    line = line.replace("%whitelist%", Boolean.toString(processInformation.getProcessGroup().getPlayerAccessConfiguration().isJoinOnlyWithPermission()));
    line = line.replace("%lobby%", Boolean.toString(processInformation.getProcessGroup().isLobbyGroup()));
    line = line.replace("%static%", Boolean.toString(processInformation.getProcessGroup().createsStaticProcesses()));
    line = line.replace("%motd%", processInformation.get("motd", String.class).orElse("empty"));

    Matcher matcher;
    while ((matcher = PATTERN.matcher(line)).find()) {
      if (2 > matcher.groupCount()) {
        continue;
      }

      line = line.replace(matcher.group(1), processInformation.get(matcher.group(2), String.class).orElse(""));
    }

    return colorize.apply(line);
  }
}
