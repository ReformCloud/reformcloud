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
package systems.reformcloud.reformcloud2.runner;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.commands.CheckForUpdatesCommand;
import systems.reformcloud.reformcloud2.runner.commands.CheckIfDevModeCommand;
import systems.reformcloud.reformcloud2.runner.commands.CheckIfSnapshotApplyCommand;
import systems.reformcloud.reformcloud2.runner.commands.ExecuteCommand;
import systems.reformcloud.reformcloud2.runner.commands.IfCommand;
import systems.reformcloud.reformcloud2.runner.commands.PrintlnCommand;
import systems.reformcloud.reformcloud2.runner.commands.SetSystemPropertiesCommand;
import systems.reformcloud.reformcloud2.runner.commands.StartApplicationCommand;
import systems.reformcloud.reformcloud2.runner.commands.UnpackApplicationCommand;
import systems.reformcloud.reformcloud2.runner.commands.VariableCommand;
import systems.reformcloud.reformcloud2.runner.commands.WriteEnvCommand;
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

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;

public final class Runner {

    private final ReformScriptInterpreter interpreter = new RunnerReformScriptInterpreter();
    private final Updater applicationsUpdater;
    private final Updater cloudVersionUpdater;
    private final String[] args;

    protected Runner(@NotNull String[] args) {
        this.interpreter
            .registerInterpreterCommand(new CheckForUpdatesCommand(this))
            .registerInterpreterCommand(new CheckIfDevModeCommand())
            .registerInterpreterCommand(new CheckIfSnapshotApplyCommand())
            .registerInterpreterCommand(new ExecuteCommand())
            .registerInterpreterCommand(new IfCommand())
            .registerInterpreterCommand(new PrintlnCommand())
            .registerInterpreterCommand(new SetSystemPropertiesCommand())
            .registerInterpreterCommand(new StartApplicationCommand(this))
            .registerInterpreterCommand(new UnpackApplicationCommand())
            .registerInterpreterCommand(new VariableCommand())
            .registerInterpreterCommand(new WriteEnvCommand())

            .registerInterpreterVariable(new EnvNotAPIVariable())
            .registerInterpreterVariable(new EnvSetVariable())
            .registerInterpreterVariable(new GitCommitVariable())
            .registerInterpreterVariable(new SetupRequiredVariable());

        this.applicationsUpdater = new ApplicationsUpdater(RunnerUtils.APP_UPDATE_FOLDER);
        this.cloudVersionUpdater = new CloudVersionUpdater(RunnerUtils.GLOBAL_REFORM_SCRIPT_FILE);
        this.args = args;
    }

    public void bootstrap() {
        if (Files.notExists(RunnerUtils.GLOBAL_REFORM_SCRIPT_FILE)) {
            RunnerUtils.copyCompiledFile("global.reformscript", RunnerUtils.GLOBAL_REFORM_SCRIPT_FILE);
        }

        InterpretedReformScript global = this.interpreter.interpret(RunnerUtils.GLOBAL_REFORM_SCRIPT_FILE);
        if (global == null) {
            throw new RuntimeException("Unable to interpret global reform script! Please recheck the syntax");
        }

        global.execute();
    }

    public void startApplication() {
        Path applicationFile = System.getProperties().containsKey("reformcloud.process.path")
            ? Path.of(System.getProperty("reformcloud.process.path"))
            : RunnerUtils.EXECUTOR_PATH;
        if (Files.notExists(applicationFile) || Files.isDirectory(applicationFile)) {
            throw new UnsupportedOperationException("Unable to start non-executable file: " + applicationFile.toString());
        }

        this.startApplication0(applicationFile);
    }

    private void startApplication0(@NotNull Path applicationFilePath) {
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
        return this.applicationsUpdater;
    }

    public Updater getCloudVersionUpdater() {
        return this.cloudVersionUpdater;
    }
}
