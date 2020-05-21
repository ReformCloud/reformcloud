/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.api.common.logger.coloured;

import java.util.regex.Pattern;

public enum Colours {

    RESET("reset", 'r', "\u001b[0m"),

    UNDERLINE("underline", 'n', "\u001b[4m"),
    BOLD("bold", 'l', "\u001b[1m"),

    GRAY("gray", '8', "\u001B[0;30;1m"),
    GREEN("green", 'a', "\u001b[32m"),
    RED("red", 'c', "\u001b[31;1m"),
    AQUA("aqua", 'b', "\u001b[36m"),
    YELLOW("yellow", 'e', "\u001b[33m"),
    PURPLE("purple", 'd', "\u001b[35;1m"),
    DARK_PURPLE("dark_purple", '5', "\u001b[35m"),
    WHITE("white", 'f', "\u001b[37m"),
    DARK_AQUA("dark_aqua", '3', "\u001b[36m"),
    DARK_BLUE("dark_blue", '1', "\u001b[34m"),
    DARK_GREEN("dark_green", '2', "\u001b[32m"),
    DARK_RED("dark_red", '4', "\u001b[31m"),
    BLUE("blue", '9', "\u001b[34m");

    private static final char COLOR_CHAR = '\u0026';

    private static final Pattern STRIP_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");
    private final String name;
    private final String ansiCode;
    private final char index;

    Colours(String name, char index, String ansiCode) {
        this.name = name;
        this.index = index;
        this.ansiCode = ansiCode;
    }

    public static String stripColor(String input) {
        return STRIP_PATTERN.matcher(input).replaceAll("");
    }

    public static String coloured(String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }

        for (Colours colours : values()) {
            text = text.replace(COLOR_CHAR + "" + colours.index, colours.ansiCode);
        }

        return text;
    }

    @Override
    public String toString() {
        return this.ansiCode;
    }

    public String getName() {
        return this.name;
    }
}
