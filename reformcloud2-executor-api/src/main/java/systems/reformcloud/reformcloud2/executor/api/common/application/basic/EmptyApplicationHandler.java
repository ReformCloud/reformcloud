package systems.reformcloud.reformcloud2.executor.api.common.application.basic;

import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationHandler;

public final class EmptyApplicationHandler implements ApplicationHandler {

  @Override
  public void onDetectApplications() {}

  @Override
  public void onInstallApplications() {}

  @Override
  public void onLoadApplications() {}

  @Override
  public void onEnableApplications() {}

  @Override
  public void onDisableApplications() {}
}
