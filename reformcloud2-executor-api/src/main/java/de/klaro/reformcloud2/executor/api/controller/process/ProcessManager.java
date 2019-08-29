package de.klaro.reformcloud2.executor.api.controller.process;

import de.klaro.reformcloud2.executor.api.common.configuration.Configurable;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Template;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.List;
import java.util.UUID;

public interface ProcessManager extends Iterable<ProcessInformation> {

    List<ProcessInformation> getAllProcesses();

    List<ProcessInformation> getProcesses(String group);

    List<Template> getOnlineAndWaiting(String group);

    ProcessInformation updateProcessInfo(ProcessInformation processInformation);

    ProcessInformation getProcess(String name);

    ProcessInformation getProcess(UUID uniqueID);

    ProcessInformation startProcess(String groupName);

    ProcessInformation startProcess(String groupName, String template);

    ProcessInformation startProcess(String groupName, String template, Configurable configurable);

    ProcessInformation stopProcess(String name);

    ProcessInformation stopProcess(UUID uniqueID);
}
