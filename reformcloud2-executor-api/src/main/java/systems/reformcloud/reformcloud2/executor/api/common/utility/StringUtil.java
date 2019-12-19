package systems.reformcloud.reformcloud2.executor.api.common.utility;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;

public final class StringUtil {

  public static final String RUNNER_DOWNLOAD_URL =
      "https://internal.reformcloud.systems/runner.jar";

  public static final String[] RC_COMMAND_HELP = new String[] {
      "rc maintenance <group>",
      "rc copy <uuid | name>",
      "rc screen <uuid | name> toggle",
      "rc list",
      "rc list <group>",
      "rc clients",
      "rc listgroups <main | sub>",
      "rc versions",
      "rc start internalclient",
      "rc start <group>",
      "rc start <group> <amount>",
      "rc start <group> <amount> <template>",
      "rc stop internalclient",
      "rc stop <name>",
      "rc stop <uuid>",
      "rc stopall <subGroup>",
      "rc ofAll <mainGroup> <list | stop>",
      "rc execute <name | uuid> <command>",
      "rc create internalclient <start-host>",
      "rc create main <name>",
      "rc create sub <name>",
      "rc create sub <name> <version>",
      "rc create sub <name> <version> <parent>",
      "rc create sub <name> <version> <parent> <static>",
      "rc create sub <name> <version> <parent> <static> <lobby>",
      "rc create sub <name> <version> <parent> <static> <minonline> <maxonline>",
      "rc create sub <name> <version> <parent> <static> <lobby> <minonline> <maxonline>",
      "rc delete <sub | main> <name>",
      "rc delete internalclient"};

  public static void sendHeader() {
    System.out.println(
        "\n"
        +
        "    __       __                        ___ _                 _ ____  \n"
        +
        "   /__\\ ___ / _| ___  _ __ _ __ ___   / __\\ | ___  _   _  __| |___ \\ \n"
        +
        "  / \\/// _ \\ |_ / _ \\| '__| '_ ` _ \\ / /  | |/ _ \\| | | |/ _` | __) |\n"
        +
        " / _  \\  __/  _| (_) | |  | | | | | / /___| | (_) | |_| | (_| |/ __/ \n"
        +
        " \\/ \\_/\\___|_|  \\___/|_|  |_| |_| |_\\____/|_|\\___/ \\__,_|\\__,_|_____|\n"
        + " \n"
        + "                   Not just a cloud system, but an experience.\n");
  }

  public static String generateString(int times) {
    Conditions.isTrue(times > 0);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < times; i++) {
      stringBuilder.append(UUID.randomUUID().toString().replace("-", ""));
    }

    return stringBuilder.toString();
  }
}
