package systems.reformcloud.reformcloud2.executor.node;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;

public final class NodeLauncher {

    public static synchronized void main(String[] args) {
        LanguageWorker.doLoad();
        DependencyLoader.doLoad();

        new NodeExecutor();
    }
}
