package systems.reformcloud.reformcloud2.executor;

import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.client.ClientLauncher;
import systems.reformcloud.reformcloud2.executor.controller.ControllerLauncher;
import systems.reformcloud.reformcloud2.executor.node.NodeLauncher;

public final class ExecutorChooser {

  public static synchronized void main(String[] args) {
    ExecutorType executor = ExecutorType.getByID(
        toID(System.getProperty("reformcloud.executor.type", "-1")));
    if (!executor.isSupported()) {
      throw new RuntimeException("Unsupported executor used!");
    }

    switch (executor) {
    case CONTROLLER: {
      ControllerLauncher.main(args);
      break;
    }

    case CLIENT: {
      ClientLauncher.main(args);
      break;
    }

    case NODE: {
      NodeLauncher.main(args);
      break;
    }
    }
  }

  private static Integer toID(String convert) {
    try {
      return Integer.parseInt(convert);
    } catch (final Throwable throwable) {
      // May cause if system variable is not set properly
      return -1;
    }
  }
}
