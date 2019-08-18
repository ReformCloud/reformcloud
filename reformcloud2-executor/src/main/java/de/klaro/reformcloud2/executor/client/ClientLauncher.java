package de.klaro.reformcloud2.executor.client;

import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;

public final class ClientLauncher {

    public static synchronized void main(String[] args) {
        StringUtil.sendHeader();
        DependencyLoader.doLoad();
    }
}
