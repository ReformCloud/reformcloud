package systems.reformcloud.reformcloud2.executor.controller;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;

public final class ControllerLauncher {

    public static synchronized void main(String[] args) {
        DependencyLoader.doLoad();
        LanguageWorker.doLoad();

        new ControllerExecutor();
    }
}
