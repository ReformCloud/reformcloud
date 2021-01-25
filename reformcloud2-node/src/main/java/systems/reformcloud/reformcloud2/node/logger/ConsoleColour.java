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
package systems.reformcloud.reformcloud2.node.logger;

import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;

import java.util.regex.Pattern;

public enum ConsoleColour {

  BLACK('0', Ansi.ansi().reset().fg(Ansi.Color.BLACK).boldOff().toString()),
  DARK_BLUE('1', Ansi.ansi().reset().fg(Ansi.Color.BLUE).boldOff().toString()),
  DARK_GREEN('2', Ansi.ansi().reset().fg(Ansi.Color.GREEN).boldOff().toString()),
  DARK_AQUA('3', Ansi.ansi().reset().fg(Ansi.Color.CYAN).boldOff().toString()),
  DARK_RED('4', Ansi.ansi().reset().fg(Ansi.Color.RED).boldOff().toString()),
  DARK_PURPLE('5', Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).boldOff().toString()),
  GOLD('6', Ansi.ansi().reset().fg(Ansi.Color.YELLOW).boldOff().toString()),
  GRAY('7', Ansi.ansi().reset().fg(Ansi.Color.WHITE).boldOff().toString()),
  DARK_GRAY('8', Ansi.ansi().reset().fg(Ansi.Color.BLACK).bold().toString()),
  BLUE('9', Ansi.ansi().reset().fg(Ansi.Color.BLUE).bold().toString()),
  GREEN('a', Ansi.ansi().reset().fg(Ansi.Color.GREEN).bold().toString()),
  AQUA('b', Ansi.ansi().reset().fg(Ansi.Color.CYAN).bold().toString()),
  RED('c', Ansi.ansi().reset().fg(Ansi.Color.RED).bold().toString()),
  LIGHT_PURPLE('d', Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).bold().toString()),
  YELLOW('e', Ansi.ansi().reset().fg(Ansi.Color.YELLOW).bold().toString()),
  WHITE('f', Ansi.ansi().reset().fg(Ansi.Color.WHITE).bold().toString()),

  MAGIC('k', Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString()),
  BOLD('l', Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString()),
  STRIKETHROUGH('m', Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString()),
  UNDERLINE('n', Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString()),
  ITALIC('o', Ansi.ansi().a(Ansi.Attribute.ITALIC).toString()),
  RESET('r', Ansi.ansi().reset().toString());

  public static final char COLOR_CHAR = '§';
  public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
  public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");

  private final String toString;
  private final String ansi;
  private final Pattern pattern;

  ConsoleColour(char code, @NotNull String ansi) {
    this.ansi = ansi;
    this.toString = new String(new char[]{COLOR_CHAR, code});
    this.pattern = Pattern.compile("(?i)" + this.toString());
  }

  @NotNull
  public static String stripColor(@NotNull String input) {
    return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
  }

  @NotNull
  public static String stripColor(char altColorChar, @NotNull String input) {
    return stripColor(toColouredString(altColorChar, input));
  }

  @NotNull
  public static String toColouredString(char altColorChar, @NotNull String textToTranslate) {
    char[] b = textToTranslate.toCharArray();
    for (int i = 0; i < b.length - 1; i++) {
      if (b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1) {
        b[i] = ConsoleColour.COLOR_CHAR;
        b[i + 1] = Character.toLowerCase(b[i + 1]);
      }
    }

    String s = new String(b);
    for (ConsoleColour value : EnumUtil.getEnumEntries(ConsoleColour.class)) {
      s = value.getPattern().matcher(s).replaceAll(value.getAnsi());
    }

    return s;
  }

  @NotNull
  public String getAnsi() {
    return this.ansi;
  }

  @NotNull
  public Pattern getPattern() {
    return this.pattern;
  }

  @Override
  public String toString() {
    return this.toString;
  }
}
