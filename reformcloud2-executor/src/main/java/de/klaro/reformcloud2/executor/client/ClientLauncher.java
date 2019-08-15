package de.klaro.reformcloud2.executor.client;

import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;

public final class ClientLauncher {

    public static synchronized void main(String[] args) {
        DependencyLoader.doLoad();
    }
}
