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
package systems.reformcloud.reformcloud2.node.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.basic.FileTemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.shared.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.shared.parser.Parsers;
import systems.reformcloud.reformcloud2.shared.collect.Entry2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class CommandTemplate implements Command {

    @Override
    public void process(@NotNull CommandSender sender, String[] strings, @NotNull String commandLine) {
        if (strings.length == 0) {
            this.describeCommandToSender(sender);
            return;
        }

        if (strings.length == 1 || strings[0].equalsIgnoreCase("versions")) {
            StringBuilder stringBuilder = new StringBuilder();
            EnumSet<Version> versions = EnumUtil.getEnumEntries(Version.class);

            int index = 0;
            for (Version version : versions) {
                stringBuilder.append(version);
                if (index++ != 0 && index % 3 == 0) {
                    stringBuilder.append(",\n");
                } else {
                    stringBuilder.append(", ");
                }
            }

            sender.sendMessages(stringBuilder.substring(0, stringBuilder.length() - 1).split("\n"));
            return;
        }

        Entry2<ProcessGroup, String> entry = this.parseTemplate(strings[0]);
        if (entry == null) {
            sender.sendMessage(LanguageManager.get("command-template-unable-to-parse", strings[0]));
            return;
        }

        Template template = entry.getFirst().getTemplate(entry.getSecond());

        if (strings.length == 2) {
            if (strings[1].equalsIgnoreCase("create")) {
                if (template != null) {
                    sender.sendMessage(LanguageManager.get(
                        "command-template-template-already-exists",
                        entry.getSecond(),
                        entry.getFirst().getName()
                    ));
                    return;
                }

                entry.getFirst().getTemplates().add(new Template(
                    0,
                    entry.getSecond(),
                    false,
                    FileTemplateBackend.NAME,
                    "-",
                    new RuntimeConfiguration(512, new ArrayList<>(), new HashMap<>()),
                    Version.PAPER_1_16_3
                ));
                ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());

                sender.sendMessage(LanguageManager.get(
                    "command-template-template-created",
                    entry.getFirst().getName(),
                    entry.getSecond()
                ));
                return;
            }

            if (strings[1].equalsIgnoreCase("info")) {
                if (template == null) {
                    sender.sendMessage(LanguageManager.get(
                        "command-template-template-not-exists",
                        entry.getSecond(),
                        entry.getFirst().getName()
                    ));
                    return;
                }

                this.describeTemplateToSender(sender, template);
                return;
            }

            if (strings[1].equalsIgnoreCase("delete")) {
                if (template == null) {
                    sender.sendMessage(LanguageManager.get(
                        "command-template-template-not-exists",
                        entry.getSecond(),
                        entry.getFirst().getName()
                    ));
                    return;
                }

                entry.getFirst().getTemplates().remove(template);
                ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
                sender.sendMessage(LanguageManager.get(
                    "command-template-template-deleted",
                    entry.getSecond(),
                    entry.getFirst().getName()
                ));
                return;
            }

            this.describeCommandToSender(sender);
            return;
        }

        if (template == null) {
            sender.sendMessage(LanguageManager.get(
                "command-template-template-not-exists",
                entry.getSecond(),
                entry.getFirst().getName()
            ));
            return;
        }

        if (strings[1].equalsIgnoreCase("addprocessparameter")) {
            if (template.getRuntimeConfiguration().getProcessParameters().contains(strings[2])) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-process-parameter-already-set",
                    strings[2], template.getName()
                ));
                return;
            }

            template.getRuntimeConfiguration().getProcessParameters().add(strings[2]);
            ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
            sender.sendMessage(LanguageManager.get("command-template-process-parameter-added", strings[2], entry.getSecond()));
            return;
        }

        if (strings[1].equalsIgnoreCase("addjvmoption")) {
            if (template.getRuntimeConfiguration().getJvmOptions().contains(strings[2])) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-process-jvm-option-already-set",
                    strings[2], template.getName()
                ));
                return;
            }

            template.getRuntimeConfiguration().getJvmOptions().add(strings[2]);
            ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
            sender.sendMessage(LanguageManager.get("command-template-process-jvm-option-added", strings[2], entry.getSecond()));
            return;
        }

        if (strings[1].equalsIgnoreCase("addshutdowncommand")) {
            if (template.getRuntimeConfiguration().getShutdownCommands().contains(strings[2])) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-process-shutdown-command-already-set",
                    strings[2], template.getName()
                ));
                return;
            }

            template.getRuntimeConfiguration().getShutdownCommands().add(strings[2]);
            ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
            sender.sendMessage(LanguageManager.get("command-template-process-shutdown-command-added", strings[2], entry.getSecond()));
            return;
        }

        if (strings[1].equalsIgnoreCase("addsystemproperty")) {
            String[] split = strings[2].split("=");
            if (split.length != 2) {
                sender.sendMessage(LanguageManager.get("command-template-system-property-not-parseable", strings[2]));
                return;
            }

            if (template.getRuntimeConfiguration().getSystemProperties().containsKey(split[0])) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-process-system-property-already-set",
                    strings[2], template.getName()
                ));
                return;
            }

            template.getRuntimeConfiguration().getSystemProperties().put(split[0], split[1]);
            ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
            sender.sendMessage(LanguageManager.get("command-template-process-system-property-added", strings[2], entry.getSecond()));
            return;
        }

        if (strings[1].equalsIgnoreCase("removeprocessparameter")) {
            if (!template.getRuntimeConfiguration().getProcessParameters().contains(strings[2])) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-process-parameter-not-set",
                    strings[2], template.getName()
                ));
                return;
            }

            template.getRuntimeConfiguration().getProcessParameters().remove(strings[2]);
            ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
            sender.sendMessage(LanguageManager.get("command-template-process-parameter-removed", strings[2], entry.getSecond()));
            return;
        }

        if (strings[1].equalsIgnoreCase("removejvmoption")) {
            if (!template.getRuntimeConfiguration().getJvmOptions().contains(strings[2])) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-process-jvm-option-not-set",
                    strings[2], template.getName()
                ));
                return;
            }

            template.getRuntimeConfiguration().getJvmOptions().remove(strings[2]);
            ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
            sender.sendMessage(LanguageManager.get("command-template-process-jvm-option-removed", strings[2], entry.getSecond()));
            return;
        }

        if (strings[1].equalsIgnoreCase("removeshutdowncommand")) {
            if (!template.getRuntimeConfiguration().getShutdownCommands().contains(strings[2])) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-process-shutdown-command-not-set",
                    strings[2], template.getName()
                ));
                return;
            }

            template.getRuntimeConfiguration().getShutdownCommands().remove(strings[2]);
            ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
            sender.sendMessage(LanguageManager.get("command-template-process-shutdown-command-removed", strings[2], entry.getSecond()));
            return;
        }

        if (strings[1].equalsIgnoreCase("removesystemproperty")) {
            if (!template.getRuntimeConfiguration().getSystemProperties().containsKey(strings[2])) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-process-system-property-not-set",
                    strings[2], template.getName()
                ));
                return;
            }

            template.getRuntimeConfiguration().getSystemProperties().remove(strings[2]);
            ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
            sender.sendMessage(LanguageManager.get("command-template-process-system-property-removed", strings[2], entry.getSecond()));
            return;
        }

        if (strings.length == 5 && strings[1].equalsIgnoreCase("removetemplateinclusion")) {
            Inclusion.InclusionLoadType type = EnumUtil.findEnumFieldByName(Inclusion.InclusionLoadType.class, strings[4]).orElse(null);
            if (type == null) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-inclusion-load-type-unknown",
                    strings[4], Streams.map(EnumUtil.getEnumEntries(Inclusion.InclusionLoadType.class), Enum::name)
                ));
                return;
            }

            for (Inclusion inclusion : template.getTemplateInclusions()) {
                if (inclusion.getKey().equalsIgnoreCase(strings[2]) && inclusion.getBackend().equalsIgnoreCase(strings[3])
                    && inclusion.getInclusionLoadType() == type) {
                    template.getTemplateInclusions().remove(inclusion);
                    ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
                    sender.sendMessage(LanguageManager.get("command-template-template-inclusion-removed", inclusion.getKey(), entry.getSecond()));
                    return;
                }
            }

            sender.sendMessage(LanguageManager.get("command-template-unable-to-remove-template-inclusion",
                strings[2], strings[3], strings[4], entry.getSecond()));
            return;
        }

        if (strings.length == 5 && strings[1].equalsIgnoreCase("removepathinclusion")) {
            Inclusion.InclusionLoadType type = EnumUtil.findEnumFieldByName(Inclusion.InclusionLoadType.class, strings[4]).orElse(null);
            if (type == null) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-inclusion-load-type-unknown",
                    strings[4], Streams.map(EnumUtil.getEnumEntries(Inclusion.InclusionLoadType.class), Enum::name)
                ));
                return;
            }

            for (Inclusion pathInclusion : template.getPathInclusions()) {
                if (pathInclusion.getKey().equalsIgnoreCase(strings[2]) && pathInclusion.getBackend().equalsIgnoreCase(strings[3])
                    && pathInclusion.getInclusionLoadType() == type) {
                    template.getPathInclusions().remove(pathInclusion);
                    ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
                    sender.sendMessage(LanguageManager.get("command-template-path-inclusion-removed", pathInclusion.getKey(), entry.getSecond()));
                    return;
                }
            }

            sender.sendMessage(LanguageManager.get("command-template-unable-to-remove-path-inclusion",
                strings[2], strings[3], strings[4], entry.getSecond()));
            return;
        }

        if (strings.length == 5 && strings[1].equalsIgnoreCase("addpathinclusion")) {
            Inclusion.InclusionLoadType type = EnumUtil.findEnumFieldByName(Inclusion.InclusionLoadType.class, strings[4]).orElse(null);
            if (type == null) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-inclusion-load-type-unknown",
                    strings[4], Streams.map(EnumUtil.getEnumEntries(Inclusion.InclusionLoadType.class), Enum::name)
                ));
                return;
            }

            for (Inclusion pathInclusion : template.getPathInclusions()) {
                if (pathInclusion.getKey().equalsIgnoreCase(strings[2]) && pathInclusion.getBackend().equalsIgnoreCase(strings[3])) {
                    sender.sendMessage(LanguageManager.get("command-template-path-inclusion-already-added", strings[2], template.getName()));
                    return;
                }
            }

            template.getPathInclusions().add(new Inclusion(strings[2], strings[3], type));
            ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
            sender.sendMessage(LanguageManager.get("command-template-path-inclusion-added", strings[2], entry.getSecond()));
            return;
        }

        if (strings.length == 5 && strings[1].equalsIgnoreCase("addtemplateinclusion")) {
            Inclusion.InclusionLoadType type = EnumUtil.findEnumFieldByName(Inclusion.InclusionLoadType.class, strings[4]).orElse(null);
            if (type == null) {
                sender.sendMessage(LanguageManager.get(
                    "command-template-inclusion-load-type-unknown",
                    strings[4], Streams.map(EnumUtil.getEnumEntries(Inclusion.InclusionLoadType.class), Enum::name)
                ));
                return;
            }

            for (Inclusion inclusion : template.getTemplateInclusions()) {
                if (inclusion.getKey().equalsIgnoreCase(strings[2]) && inclusion.getBackend().equalsIgnoreCase(strings[3])) {
                    sender.sendMessage(LanguageManager.get("command-template-template-inclusion-already-added", strings[2], template.getName()));
                    return;
                }
            }

            if (this.parseTemplate(strings[2]) == null) {
                sender.sendMessage(LanguageManager.get("command-template-unable-to-parse", strings[2]));
                return;
            }

            template.getTemplateInclusions().add(new Inclusion(strings[2], strings[3], type));
            ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(entry.getFirst());
            sender.sendMessage(LanguageManager.get("command-template-template-inclusion-added", strings[2], entry.getSecond()));
            return;
        }

        if (strings[1].equalsIgnoreCase("edit")) {
            this.handleEditCall(sender, template, entry.getFirst(), strings);
            return;
        }

        this.describeCommandToSender(sender);
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender commandSender, String[] strings, int bufferIndex, @NotNull String commandLine) {
        List<String> result = new ArrayList<>();
        if (bufferIndex == 0) {
            result.add("versions");
            for (ProcessGroup processGroup : ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()) {
                for (Template template : processGroup.getTemplates()) {
                    result.add(processGroup.getName() + "/" + template.getName());
                }
            }
        } else if (bufferIndex == 1) {
            result.addAll(Arrays.asList(
                "create", "info", "delete", "addProcessParameter", "addJvmOption",
                "addSystemProperty", "addShutdownCommand", "removeProcessParameter",
                "removeJvmOption", "removeSystemProperty", "removeShutdownCommand",
                "addPathInclusion", "addTemplateInclusion", "removePathInclusion",
                "removeTemplateInclusion", "edit"
            ));
        } else if (bufferIndex >= 2 && strings[1].equalsIgnoreCase("edit")) {
            result.addAll(Arrays.asList(
                "--version=PAPER_1_16_1", "--priority=1", "--global=true", "--autoReleaseOnClose=false",
                "--backend=FILE", "--serverNameSplitter=-", "--max-memory=512", "--dynamic-memory=-1"
            ));
        }

        return result;
    }

    private @Nullable Entry2<ProcessGroup, String> parseTemplate(@NotNull String in) {
        String[] split = in.split("/");
        if (split.length != 2) {
            return null;
        }

        return ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(split[0])
            .map(processGroup -> new Entry2<>(processGroup, split[1]))
            .orElse(null);
    }

    private void describeCommandToSender(@NotNull CommandSender source) {
        source.sendMessages((
            "template versions                                                                          | Lists all versions which are available to the console\n" +
                "template <group>/<template> [create]                                                       | Creates the specified template\n" +
                "template <group>/<template> [info]                                                         | Shows information about the specified template\n" +
                "template <group>/<template> [delete]                                                       | Deletes the specified template\n" +
                "template <group>/<template> addProcessParameter <parameter>                                | Adds the given parameter to the given template\n" +
                "template <group>/<template> addJvmOption <option>                                          | Adds the given jvm-option to the given template\n" +
                "template <group>/<template> addSystemProperty <key>=<value>                                | Adds the system property parameter to the given template\n" +
                "template <group>/<template> addShutdownCommand <command>                                   | Adds the given shutdown command to the given template\n" +
                "template <group>/<template> removeProcessParameter <parameter>                             | Adds the given parameter to the given template\n" +
                "template <group>/<template> removeJvmOption <option>                                       | Removes the given jvm option from the given template\n" +
                "template <group>/<template> removeSystemProperty <key>                                     | Removes the given system property from the given template\n" +
                "template <group>/<template> removeShutdownCommand <command>                                | Removes the given shutdown command from the given template\n" +
                "template <group>/<template> addPathInclusion <path> <backend> <priority>                   | Adds the path inclusion for the given backend to the specified template\n" +
                "template <group>/<template> addTemplateInclusion <group/template> <backend> <priority>     | Adds the template inclusion for the given backend to the specified template\n" +
                "template <group>/<template> removePathInclusion <path> <backend> <priority>                | Removes the path inclusion for the given backend from the specified template\n" +
                "template <group>/<template> removeTemplateInclusion <group/template> <backend> <priority>  | Removes the template inclusion for the given backend from the specified template\n" +
                "\n" +
                "template <group>/<template> [edit]                                                         | Edits the specified template - the edit options are listed below\n" +
                "   --version=<Version>                                                                     | Sets the version of the specified template\n" +
                "   --priority=<priority>                                                                   | Sets the priority of the specified template\n" +
                "   --global=<global>                                                                       | Sets weather the template should be global or not\n" +
                "   --autoReleaseOnClose=<autoReleaseOnClose>                                               | Sets weather the template should get copied to the backend after every process stop\n" +
                "   --backend=<backend>                                                                     | Sets the backend of the template\n" +
                "   --serverNameSplitter=<serverNameSplitter>                                               | Sets the server name splitter of the template\n" +
                "   --max-memory=<memory>                                                                   | Sets the max memory of the template\n" +
                "   --dynamic-memory=<dynamicMemory>                                                        | Sets the dynamic memory of the template"
        ).split("\n"));
    }

    private void describeTemplateToSender(@NotNull CommandSender sender, @NotNull Template template) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(" Name                  - ").append(template.getName()).append("\n");
        stringBuilder.append(" Backend               - ").append(template.getBackend()).append("\n");
        stringBuilder.append(" Version               - ").append(template.getVersion().name()).append("\n");
        stringBuilder.append(" Max-Memory            - ").append(template.getRuntimeConfiguration().getMaxMemory()).append("\n");
        stringBuilder.append(" Dynamic-Memory        - ").append(template.getRuntimeConfiguration().getDynamicMemory()).append("\n");
        stringBuilder.append(" Priority              - ").append(template.getPriority()).append("\n");
        stringBuilder.append(" Global                - ").append(template.isGlobal() ? "yes" : "no").append("\n");
        stringBuilder.append(" Auto Release on Close - ").append(template.isAutoReleaseOnClose() ? "yes" : "no").append("\n");

        stringBuilder.append(" JVM-Options:").append("\n");
        for (String jvmOption : template.getRuntimeConfiguration().getJvmOptions()) {
            stringBuilder.append("  - ").append(jvmOption).append("\n");
        }

        stringBuilder.append(" Process Parameters:").append("\n");
        for (String processParameter : template.getRuntimeConfiguration().getProcessParameters()) {
            stringBuilder.append("  - ").append(processParameter).append("\n");
        }

        stringBuilder.append(" System-Properties:").append("\n");
        for (Map.Entry<String, String> stringStringEntry : template.getRuntimeConfiguration().getSystemProperties().entrySet()) {
            stringBuilder.append("  - ").append(stringStringEntry.getKey()).append(" = ").append(stringStringEntry.getValue()).append("\n");
        }

        stringBuilder.append(" Template Inclusions:").append("\n");
        for (Inclusion templateInclusion : template.getTemplateInclusions()) {
            stringBuilder.append("  - ").append(templateInclusion.getKey())
                .append(" | Backend: ").append(templateInclusion.getBackend())
                .append(" | Load-Order: ").append(templateInclusion.getInclusionLoadType().name())
                .append("\n");
        }

        stringBuilder.append(" Path Inclusions:").append("\n");
        for (Inclusion pathInclusion : template.getPathInclusions()) {
            stringBuilder.append("  - ").append(pathInclusion.getKey())
                .append(" | Backend: ").append(pathInclusion.getBackend())
                .append(" | Load-Order: ").append(pathInclusion.getInclusionLoadType().name())
                .append("\n");
        }

        sender.sendMessages(stringBuilder.toString().split("\n"));
    }

    private void handleEditCall(@NotNull CommandSender sender, @NotNull Template template, @NotNull ProcessGroup group, @NotNull String[] strings) {
        Properties properties = StringUtil.parseProperties(strings, 2);
        if (properties.containsKey("version")) {
            Version version = EnumUtil.findEnumFieldByName(Version.class, properties.getProperty("version")).orElse(null);
            if (version == null) {
                sender.sendMessage(LanguageManager.get("command-template-unknown-version", properties.getProperty("version")));
                return;
            }

            template.setVersion(version);
            sender.sendMessage(LanguageManager.get("command-template-edit-success", "version", version.name(), template.getName()));
        }

        if (properties.containsKey("priority")) {
            Integer priority = Parsers.INT.parse(properties.getProperty("priority"));
            if (priority == null) {
                sender.sendMessage(LanguageManager.get("command-template-expected-int", properties.getProperty("priority")));
                return;
            }

            template.setPriority(priority);
            sender.sendMessage(LanguageManager.get("command-template-edit-success", "priority", priority, template.getName()));
        }

        if (properties.containsKey("global")) {
            Boolean global = Parsers.BOOLEAN.parse(properties.getProperty("global"));
            if (global == null) {
                sender.sendMessage(LanguageManager.get("command-template-expected-boolean", properties.getProperty("global")));
                return;
            }

            template.setGlobal(global);
            sender.sendMessage(LanguageManager.get("command-template-edit-success", "global", global, template.getName()));
        }

        if (properties.containsKey("autoreleaseonclose")) {
            Boolean autoReleaseOnClose = Parsers.BOOLEAN.parse(properties.getProperty("autoreleaseonclose"));
            if (autoReleaseOnClose == null) {
                sender.sendMessage(LanguageManager.get("command-template-expected-boolean", properties.getProperty("autoreleaseonclose")));
                return;
            }

            template.setAutoReleaseOnClose(autoReleaseOnClose);
            sender.sendMessage(LanguageManager.get("command-template-edit-success", "auto release on close", autoReleaseOnClose, template.getName()));
        }

        if (properties.containsKey("backend")) {
            if (TemplateBackendManager.get(properties.getProperty("backend")).isEmpty()) {
                sender.sendMessage(LanguageManager.get("command-template-backend-not-loaded", properties.getProperty("backend")));
                return;
            }

            template.setBackend(properties.getProperty("backend"));
            sender.sendMessage(LanguageManager.get("command-template-edit-success", "backend", properties.getProperty("backend"), template.getName()));
        }

        if (properties.containsKey("servernamesplitter")) {
            String splitter = properties.getProperty("servernamesplitter");
            template.setServerNameSplitter(splitter);
            sender.sendMessage(LanguageManager.get("command-template-edit-success", "server name splitter", splitter, template.getName()));
        }

        if (properties.containsKey("max-memory")) {
            Integer memory = Parsers.INT.parse(properties.getProperty("max-memory"));
            if (memory == null) {
                sender.sendMessage(LanguageManager.get("command-template-expected-int", properties.getProperty("max-memory")));
                return;
            }

            if (memory < 50) {
                sender.sendMessage(LanguageManager.get("command-template-memory-very-low", memory, "50"));
                return;
            }

            template.getRuntimeConfiguration().setMaxMemory(memory);
            sender.sendMessage(LanguageManager.get("command-template-edit-success", "max memory", memory, template.getName()));
        }

        if (properties.containsKey("dynamic-memory")) {
            Integer memory = Parsers.INT.parse(properties.getProperty("dynamic-memory"));
            if (memory == null) {
                sender.sendMessage(LanguageManager.get("command-template-expected-int", properties.getProperty("dynamic-memory")));
                return;
            }

            template.getRuntimeConfiguration().setDynamicMemory(memory);
            sender.sendMessage(LanguageManager.get("command-template-edit-success", "dynamic memory", memory, template.getName()));
        }

        ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(group);
        sender.sendMessage(LanguageManager.get("command-template-edit-finish", template.getName(), group.getName()));
    }
}
