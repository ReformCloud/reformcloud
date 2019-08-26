package de.klaro.reformcloud2.executor.api.common.process;

import de.klaro.reformcloud2.executor.api.common.configuration.Configurable;
import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Template;
import de.klaro.reformcloud2.executor.api.common.plugins.Plugin;
import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;
import java.util.UUID;

public final class ProcessInformation implements Nameable {

    public ProcessInformation(String processName, String parent, UUID processUniqueID, int id,
                              ProcessState processState, NetworkInfo networkInfo,
                              ProcessGroup processGroup, Template template,
                              ProcessRuntimeInformation processRuntimeInformation,
                              List<Plugin> plugins, Configurable extra) {
        this.processName = processName;
        this.parent = parent;
        this.processUniqueID = processUniqueID;
        this.id = id;
        this.processState = processState;
        this.networkInfo = networkInfo;
        this.processGroup = processGroup;
        this.template = template;
        this.processRuntimeInformation = processRuntimeInformation;
        this.plugins = plugins;
        this.extra = extra;
    }

    private String processName;

    private String parent;

    private UUID processUniqueID;

    private int id;

    private ProcessState processState;

    private NetworkInfo networkInfo;

    private ProcessGroup processGroup;

    private Template template;

    private ProcessRuntimeInformation processRuntimeInformation;

    private List<Plugin> plugins;

    private Configurable extra;

    public String getProcessName() {
        return processName;
    }

    public String getParent() {
        return parent;
    }

    public UUID getProcessUniqueID() {
        return processUniqueID;
    }

    public int getId() {
        return id;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public ProcessGroup getProcessGroup() {
        return processGroup;
    }

    public Template getTemplate() {
        return template;
    }

    public ProcessRuntimeInformation getProcessRuntimeInformation() {
        return processRuntimeInformation;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public Configurable getExtra() {
        return extra;
    }

    @Override
    public String getName() {
        return processName;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public void setProcessRuntimeInformation(ProcessRuntimeInformation processRuntimeInformation) {
        this.processRuntimeInformation = processRuntimeInformation;
    }
}
