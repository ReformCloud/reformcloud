package systems.reformcloud.reformcloud2.runner;

import systems.reformcloud.reformcloud2.runner.commands.*;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.ReformScriptInterpreter;
import systems.reformcloud.reformcloud2.runner.reformscript.basic.RunnerReformScriptInterpreter;
import systems.reformcloud.reformcloud2.runner.updater.Updater;
import systems.reformcloud.reformcloud2.runner.updater.basic.ApplicationsUpdater;
import systems.reformcloud.reformcloud2.runner.updater.basic.CloudVersionUpdater;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;
import systems.reformcloud.reformcloud2.runner.variables.EnvNotAPIVariable;
import systems.reformcloud.reformcloud2.runner.variables.EnvSetVariable;
import systems.reformcloud.reformcloud2.runner.variables.GitCommitVariable;
import systems.reformcloud.reformcloud2.runner.variables.SetupRequiredVariable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

public final class Runner {

    private final ReformScriptInterpreter interpreter = new RunnerReformScriptInterpreter();

    private final Updater applicationsUpdater;

    private final Updater cloudVersionUpdater;

    private final String[] args;

    Runner(@Nonnull String[] args) {
        this.interpreter
                .registerInterpreterCommand(new CheckForUpdatesCommand(this))
                .registerInterpreterCommand(new CheckIfDevModeCommand())
                .registerInterpreterCommand(new CheckIfSnapshotApplyCommand())
                .registerInterpreterCommand(new ExecuteCommand())
                .registerInterpreterCommand(new IfCommand())
                .registerInterpreterCommand(new PrintlnCommand())
                .registerInterpreterCommand(new SetSystemPropertiesCommand())
                .registerInterpreterCommand(new SetupCommand())
                .registerInterpreterCommand(new StartApplicationCommand(this))
                .registerInterpreterCommand(new UnpackApplicationCommand())
                .registerInterpreterCommand(new VariableCommand())
                .registerInterpreterCommand(new WriteEnvCommand())

                .registerInterpreterVariable(new EnvNotAPIVariable())
                .registerInterpreterVariable(new EnvSetVariable())
                .registerInterpreterVariable(new GitCommitVariable())
                .registerInterpreterVariable(new SetupRequiredVariable());

        this.applicationsUpdater = new ApplicationsUpdater(RunnerUtils.APP_UPDATE_FOLDER);
        this.cloudVersionUpdater = new CloudVersionUpdater(RunnerUtils.GLOBAL_SCRIPT_FILE);
        this.args = args;
    }

    public void bootstrap() {
        if (!RunnerUtils.GLOBAL_SCRIPT_FILE.exists()) {
            RunnerUtils.copyCompiledFile("global.reformscript", RunnerUtils.GLOBAL_SCRIPT_FILE);
        }

        InterpretedReformScript global = this.interpreter.interpret(RunnerUtils.GLOBAL_SCRIPT_FILE);
        if (global == null) {
            throw new RuntimeException("Unable to interpret global reform script! Please recheck the syntax");
        }

        global.execute();
    }

    public void startApplication() {
        Path applicationFile = System.getProperties().containsKey("reformcloud.process.path")
                ? Paths.get(System.getProperty("reformcloud.process.path")) : RunnerUtils.EXECUTOR_PATH;
        if (!Files.exists(applicationFile) || Files.isDirectory(applicationFile)) {
            throw new UnsupportedOperationException("Unable to start non-executable file: " + applicationFile.toString());
        }

        this.startApplication0(applicationFile);
    }

    private void startApplication0(@Nonnull Path applicationFilePath) {
        try (JarFile file = new JarFile(applicationFilePath.toFile())) {
            URLClassLoader classLoader = new RunnerClassLoader(new URL[]{applicationFilePath.toUri().toURL()});
            Thread.currentThread().setContextClassLoader(classLoader);

            String mainClass = file.getManifest().getMainAttributes().getValue("Main-Class");
            Method main = classLoader.loadClass(mainClass).getMethod("main", String[].class);

            main.invoke(null, (Object) this.args);
        } catch (final IOException | ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Updater getApplicationsUpdater() {
        return applicationsUpdater;
    }

    public Updater getCloudVersionUpdater() {
        return cloudVersionUpdater;
    }
}
