package systems.reformcloud.reformcloud2.web;

import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.web.commands.WebCommand;
import systems.reformcloud.reformcloud2.web.tokens.TokenDatabase;
import systems.reformcloud.reformcloud2.web.tokens.TokenWebServerAuth;

public class WebApplication extends Application {

  public static final WebCommand WEB_COMMAND = new WebCommand();

  @Override
  public void onLoad() {
    TokenDatabase.load();
    getListener().setAuth(new TokenWebServerAuth());
  }

  public static RequestListenerHandler getListener() {
    return ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)
        ? NodeExecutor.getInstance().getRequestListenerHandler()
        : ControllerExecutor.getInstance().getRequestListenerHandler();
  }
}
