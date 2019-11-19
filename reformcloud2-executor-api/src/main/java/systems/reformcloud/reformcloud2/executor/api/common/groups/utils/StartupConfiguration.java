package systems.reformcloud.reformcloud2.executor.api.common.groups.utils;

import java.util.List;
import java.util.Objects;

public final class StartupConfiguration {

    public StartupConfiguration(int maxOnlineProcesses, int minOnlineProcesses, int startupPriority,
                                int startPort, StartupEnvironment startupEnvironment,
                                boolean searchBestClientAlone, List<String> useOnlyTheseClients) {
        this.maxOnlineProcesses = maxOnlineProcesses;
        this.minOnlineProcesses = minOnlineProcesses;
        this.startupPriority = startupPriority;
        this.startPort = startPort;
        this.startupEnvironment = startupEnvironment;
        this.searchBestClientAlone = searchBestClientAlone;
        this.useOnlyTheseClients = useOnlyTheseClients;
    }

    private int maxOnlineProcesses;

    private int minOnlineProcesses;

    private int startupPriority;

    private int startPort;

    private StartupEnvironment startupEnvironment;

    private AutomaticStartupConfiguration automaticStartupConfiguration = AutomaticStartupConfiguration.defaults();

    private boolean searchBestClientAlone;

    private List<String> useOnlyTheseClients;

    public int getMaxOnlineProcesses() {
        return maxOnlineProcesses;
    }

    public int getMinOnlineProcesses() {
        return minOnlineProcesses;
    }

    public int getStartupPriority() {
        return startupPriority;
    }

    public int getStartPort() {
        return startPort;
    }

    public StartupEnvironment getStartupEnvironment() {
        return startupEnvironment;
    }

    public AutomaticStartupConfiguration getAutomaticStartupConfiguration() {
        return automaticStartupConfiguration;
    }

    public boolean isSearchBestClientAlone() {
        return searchBestClientAlone;
    }

    public List<String> getUseOnlyTheseClients() {
        return useOnlyTheseClients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StartupConfiguration)) return false;
        StartupConfiguration that = (StartupConfiguration) o;
        return getMaxOnlineProcesses() == that.getMaxOnlineProcesses() &&
                getMinOnlineProcesses() == that.getMinOnlineProcesses() &&
                getStartupPriority() == that.getStartupPriority() &&
                getStartPort() == that.getStartPort() &&
                isSearchBestClientAlone() == that.isSearchBestClientAlone() &&
                getStartupEnvironment() == that.getStartupEnvironment() &&
                Objects.equals(getUseOnlyTheseClients(), that.getUseOnlyTheseClients());
    }
}
