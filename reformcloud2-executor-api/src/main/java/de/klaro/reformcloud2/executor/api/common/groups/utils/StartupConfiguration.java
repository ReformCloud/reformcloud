package de.klaro.reformcloud2.executor.api.common.groups.utils;

import java.util.List;

public final class StartupConfiguration {

    private int maxOnlineProcesses;

    private int minOnlineProcesses;

    private int startupPriority;

    private int startPort;

    private StartupEnvironment startupEnvironment;

    private boolean searchBestClientAlone;

    private List<String> useOnlyTheseClients;
}
