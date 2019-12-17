package systems.reformcloud.reformcloud2.executor.node;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;

public class NodeLauncher {

  public static synchronized void main(String[] args) {
    StringUtil.sendHeader();
    DependencyLoader.doLoad();
    LanguageWorker.doLoad();

    new NodeExecutor();
  }
}
