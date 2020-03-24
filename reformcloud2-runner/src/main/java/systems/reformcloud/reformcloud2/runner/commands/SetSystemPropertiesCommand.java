package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class SetSystemPropertiesCommand extends InterpreterCommand {

    public SetSystemPropertiesCommand() {
        super("set_system_properties");
    }

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        System.setProperty(
                "reformcloud.runner.specification",
                System.getProperty("reformcloud.runner.version").endsWith("-SNAPSHOT") ? "SNAPSHOT" : "RELEASE"
        );
    }
}
