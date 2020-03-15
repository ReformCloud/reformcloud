package systems.reformcloud.reformcloud2.executor.api.common.groups.utils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public final class StartupConfiguration {

    public StartupConfiguration(int maxOnlineProcesses, int minOnlineProcesses, int startupPriority,
                                int startPort, StartupEnvironment startupEnvironment,
                                boolean searchBestClientAlone, List<String> useOnlyTheseClients) {
        this(maxOnlineProcesses, minOnlineProcesses, startupPriority, startPort, startupEnvironment,
                AutomaticStartupConfiguration.defaults(), searchBestClientAlone, useOnlyTheseClients);
    }

    public StartupConfiguration(int maxOnlineProcesses, int minOnlineProcesses, int startupPriority,
                                int startPort, StartupEnvironment startupEnvironment,
                                AutomaticStartupConfiguration automaticStartupConfiguration,
                                boolean searchBestClientAlone, List<String> useOnlyTheseClients) {
        this(maxOnlineProcesses, minOnlineProcesses, 1, startupPriority, startPort, startupEnvironment,
                automaticStartupConfiguration, searchBestClientAlone, useOnlyTheseClients);
    }

    public StartupConfiguration(int maxOnlineProcesses, int minOnlineProcesses, int alwaysPreparedProcesses,
                                int startupPriority, int startPort, StartupEnvironment startupEnvironment,
                                AutomaticStartupConfiguration automaticStartupConfiguration,
                                boolean searchBestClientAlone, List<String> useOnlyTheseClients) {
        this.maxOnlineProcesses = maxOnlineProcesses;
        this.minOnlineProcesses = minOnlineProcesses;
        this.alwaysPreparedProcesses = alwaysPreparedProcesses;
        this.startupPriority = startupPriority;
        this.startPort = startPort;
        this.startupEnvironment = startupEnvironment;
        this.automaticStartupConfiguration = automaticStartupConfiguration;
        this.searchBestClientAlone = searchBestClientAlone;
        this.useOnlyTheseClients = useOnlyTheseClients;
    }

    private int maxOnlineProcesses;

    private int minOnlineProcesses;

    private int alwaysPreparedProcesses;

    private int startupPriority;

    private int startPort;

    private final StartupEnvironment startupEnvironment;

    private final AutomaticStartupConfiguration automaticStartupConfiguration;

    private boolean searchBestClientAlone;

    private List<String> useOnlyTheseClients;

    public int getMaxOnlineProcesses() {
        return maxOnlineProcesses;
    }

    public int getMinOnlineProcesses() {
        return minOnlineProcesses;
    }

    public int getAlwaysPreparedProcesses() {
        return alwaysPreparedProcesses;
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

    @Nonnull
    public AutomaticStartupConfiguration getAutomaticStartupConfiguration() {
        return automaticStartupConfiguration;
    }

    public boolean isSearchBestClientAlone() {
        return searchBestClientAlone;
    }

    @Nonnull
    public List<String> getUseOnlyTheseClients() {
        return useOnlyTheseClients;
    }

    public void setMaxOnlineProcesses(int maxOnlineProcesses) {
        this.maxOnlineProcesses = maxOnlineProcesses;
    }

    public void setMinOnlineProcesses(int minOnlineProcesses) {
        this.minOnlineProcesses = minOnlineProcesses;
    }

    public void setStartPort(int startPort) {
        this.startPort = startPort;
    }

    public void setStartupPriority(int startupPriority) {
        this.startupPriority = startupPriority;
    }

    public void setUseOnlyTheseClients(List<String> useOnlyTheseClients) {
        this.useOnlyTheseClients = useOnlyTheseClients;
        this.searchBestClientAlone = useOnlyTheseClients.isEmpty();
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
