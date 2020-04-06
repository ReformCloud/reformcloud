package systems.reformcloud.reformcloud2.executor.client;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;

public final class ClientLauncher {

    public static synchronized void main(String[] args) {
        LanguageWorker.doLoad();
        DependencyLoader.doLoad();

        new ClientExecutor();
    }
}
