package de.klaro.reformcloud2.executor.api.common.commands.complete;

import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.Arrays;
import java.util.Collection;

public interface TabCompleter {

    Collection<String> complete(CommandSource commandSource, String commandLine, String[] currentArg);

    default Collection<String> convert(String... strings) {
        return Arrays.asList(strings);
    }
}
