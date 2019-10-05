package systems.reformcloud.reformcloud2.executor.api.common.logger.coloured;

import org.fusesource.jansi.Ansi;

import java.util.regex.Pattern;

public enum Colours {

    RESET("reset", 'r', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.DEFAULT).boldOff().toString()),

    GREEN("green", 'a', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString()),
    LIGHT_BLUE("light_blue", 'b', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString()),
    RED("red", 'c', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString()),
    YELLOW("yellow", 'e', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString()),
    WHITE("white", 'f', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString()),
    CYAN("cyan", '3', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString()),
    DARK_RED("dark_red", '4', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString()),
    PURPLE("purple", '5', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString()),
    ORANGE("orange", '6', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString()),
    GRAY("gray", '7', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString()),
    DARK_GRAY("dark_gray", '8', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString()),
    BLUE("blue", '9', Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString()),

    BLINK("blink", 'k', Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString()),
    BOLD("bold", 'l', Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString()),
    STRIKETHROUGH("strikethrough", 'm', Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString()),
    UNDERLINE("underline", 'n', Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString()),
    ITALIC("italic", 'o', Ansi.ansi().a(Ansi.Attribute.ITALIC).toString());

    public static final char COLOR_CHAR = '&';

    private static final Pattern STRIP_PATTERN = Pattern.compile("[&][a-f0-9r]");

    Colours(String name, char index, String ansiCode) {
        this.name = name;
        this.index = index;
        this.ansiCode = ansiCode;
    }

    private final String name;

    private String ansiCode;

    private final char index;

    public static String stripColor(String input) {
        return STRIP_PATTERN.matcher(input.replaceAll("\u001B\\[[;\\d]*m", "")).replaceAll("");
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
        return ansiCode;
    }

    public String getName() {
        return name;
    }

    public String getAnsiCode() {
        return ansiCode;
    }

    public char getIndex() {
        return index;
    }
}
