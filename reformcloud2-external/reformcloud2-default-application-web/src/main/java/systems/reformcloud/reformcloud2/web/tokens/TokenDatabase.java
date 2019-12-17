package systems.reformcloud.reformcloud2.web.tokens;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

public final class TokenDatabase {

  private TokenDatabase() { throw new UnsupportedOperationException(); }

  private static final String DB = "internal_web_db";

  private static JsonConfiguration configuration;

  public static void load() {
    ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().createDatabase(
        DB);
    if (isSetupDone()) {
      configuration =
          ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(
              DB, "global_user", null);
    }
  }

  public static boolean isSetupDone() {
    return ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().contains(
        DB, "global_user");
  }

  static boolean tryAuth(String token) { return false; }

  @Nullable
  public static String trySetup(@Nonnull byte[] body) {
    if (isSetupDone() || configuration != null) {
      return null;
    }

    return null;
  }
}
