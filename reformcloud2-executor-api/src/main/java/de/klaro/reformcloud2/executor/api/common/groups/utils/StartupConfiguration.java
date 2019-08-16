package de.klaro.reformcloud2.executor.api.common.groups.utils;

import java.util.List;

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

    public boolean isSearchBestClientAlone() {
        return searchBestClientAlone;
    }

    public List<String> getUseOnlyTheseClients() {
        return useOnlyTheseClients;
    }
}
