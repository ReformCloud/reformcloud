package systems.reformcloud.reformcloud2.executor.client;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;

public final class ClientLauncher {

  public static synchronized void main(String[] args) {
    StringUtil.sendHeader();
    DependencyLoader.doLoad();
    LanguageWorker.doLoad();

    new ClientExecutor();
  }
}
