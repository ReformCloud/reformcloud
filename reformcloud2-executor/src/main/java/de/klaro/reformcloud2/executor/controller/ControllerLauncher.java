package de.klaro.reformcloud2.executor.controller;

import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;

public final class ControllerLauncher {

    public static synchronized void main(String[] args) {
        DependencyLoader.doLoad();
    }
}
