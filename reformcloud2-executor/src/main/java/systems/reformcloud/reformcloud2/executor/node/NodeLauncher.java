package systems.reformcloud.reformcloud2.executor.node;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;

public class NodeLauncher {

    public static synchronized void main(String[] args) {
        DependencyLoader.doLoad();
        LanguageWorker.doLoad();

        new NodeExecutor();
    }
}
