package systems.reformcloud.reformcloud2.signs.application;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignLayout;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignSubLayout;
import systems.reformcloud.reformcloud2.signs.util.sign.config.util.LayoutContext;

import java.util.Arrays;
import java.util.Collections;

final class ConfigHelper {

    private ConfigHelper() {
        throw new UnsupportedOperationException();
    }

    static SignConfig read(String path) {
        return JsonConfiguration.read(path + "/layout.json").get("config", SignConfig.TYPE);
    }

    static void createDefault(String path) {
        SignConfig config = new SignConfig(
                1,
                Collections.singletonList(createDefaultLayout()),
                false,
                "reformcloud.knockback.bypass",
                1.0D,
                0.8D
        );
        new JsonConfiguration()
                .add("config", config)
                .write(path + "/layout.json");
    }

    private static SignLayout createDefaultLayout() {
        return new SignLayout(
                LayoutContext.GLOBAL,
                null,
                true,
                true,
                Arrays.asList(
                        createDefault("STAINED_CLAY", 1, "< = >", "%group%", " Waiting ", "< = >"),
                        createDefault("STAINED_CLAY", 1, "< == >", "%group%", " Waiting. ", "< == >"),
                        createDefault("STAINED_CLAY", 1, "< === >", "%group%", " Waiting.. ", "< === >"),
                        createDefault("STAINED_CLAY", 1, "< ==== >", "%group%", " Waiting... ", "< ==== >"),
                        createDefault("STAINED_CLAY", 1, "< ===== >", "%group%", " Waiting.... ", "< ===== >"),
                        createDefault("STAINED_CLAY", 1, "< ====== >", "%group%", " Waiting..... ", "< ====== >"),
                        createDefault("STAINED_CLAY", 1, "< ======= >", "%group%", " Waiting.... ", "< ======= >"),
                        createDefault("STAINED_CLAY", 1, "< ======== >", "%group%", " Waiting... ", "< ======== >"),
                        createDefault("STAINED_CLAY", 1, "< ========= >", "%group%", " Waiting.. ", "< ========= >"),
                        createDefault("STAINED_CLAY", 1, "< ========== >", "%group%", " Waiting. ", "< ========== >"),
                        createDefault("STAINED_CLAY", 1, "< =========== >", "%group%", " Waiting ", "< =========== >")
                ), Arrays.asList(
                        createDefault("STAINED_CLAY", 2, "< = >", "%group%", " Connecting ", "< = >"),
                        createDefault("STAINED_CLAY", 2, "< == >", "%group%", " Connecting. ", "< == >"),
                        createDefault("STAINED_CLAY", 2, "< === >", "%group%", " Connecting.. ", "< === >"),
                        createDefault("STAINED_CLAY", 2, "< ==== >", "%group%", " Connecting... ", "< ==== >"),
                        createDefault("STAINED_CLAY", 2, "< ===== >", "%group%", " Connecting.... ", "< ===== >"),
                        createDefault("STAINED_CLAY", 2, "< ====== >", "%group%", " Connecting..... ", "< ====== >"),
                        createDefault("STAINED_CLAY", 2, "< ======= >", "%group%", " Connecting.... ", "< ======= >"),
                        createDefault("STAINED_CLAY", 2, "< ======== >", "%group%", " Connecting... ", "< ======== >"),
                        createDefault("STAINED_CLAY", 2, "< ========= >", "%group%", " Connecting.. ", "< ========= >"),
                        createDefault("STAINED_CLAY", 2, "< ========== >", "%group%", " Connecting. ", "< ========== >"),
                        createDefault("STAINED_CLAY", 2, "< =========== >", "%group%", " Connecting ", "< =========== >")
                ), Arrays.asList(
                        createDefault("STAINED_CLAY", 7, "< = >", "%name%", "%template%", "< = >"),
                        createDefault("STAINED_CLAY", 7, "< == >", "%name%", "%template%", "< == >"),
                        createDefault("STAINED_CLAY", 7, "< === >", "%name%", "%template%", "< === >"),
                        createDefault("STAINED_CLAY", 7, "< ==== >", "%name%", "%template%", "< ==== >"),
                        createDefault("STAINED_CLAY", 7, "< ===== >", "%name%", "%template%", "< ===== >"),
                        createDefault("STAINED_CLAY", 7, "< ====== >", "%name%", "%template%", "< ====== >"),
                        createDefault("STAINED_CLAY", 7, "< ======= >", "%name%", "%online%/%max%", "< ======= >"),
                        createDefault("STAINED_CLAY", 7, "< ======== >", "%name%", "%online%/%max%", "< ======== >"),
                        createDefault("STAINED_CLAY", 7, "< ========= >", "%name%", "%online%/%max%", "< ========= >"),
                        createDefault("STAINED_CLAY", 7, "< ========== >", "%name%", "%online%/%max%", "< ========== >"),
                        createDefault("STAINED_CLAY", 7, "< =========== >", "%name%", "%online%/%max%", "< =========== >")
                ), Arrays.asList(
                        createDefault("STAINED_CLAY", 13, "< = >", "%name%", "%template%", "< = >"),
                        createDefault("STAINED_CLAY", 13, "< == >", "%name%", "%template%", "< == >"),
                        createDefault("STAINED_CLAY", 13, "< === >", "%name%", "%template%", "< === >"),
                        createDefault("STAINED_CLAY", 13, "< ==== >", "%name%", "%template%", "< ==== >"),
                        createDefault("STAINED_CLAY", 13, "< ===== >", "%name%", "%template%", "< ===== >"),
                        createDefault("STAINED_CLAY", 13, "< ====== >", "%name%", "%template%", "< ====== >"),
                        createDefault("STAINED_CLAY", 13, "< ======= >", "%name%", "%online%/%max%", "< ======= >"),
                        createDefault("STAINED_CLAY", 13, "< ======== >", "%name%", "%online%/%max%", "< ======== >"),
                        createDefault("STAINED_CLAY", 13, "< ========= >", "%name%", "%online%/%max%", "< ========= >"),
                        createDefault("STAINED_CLAY", 13, "< ========== >", "%name%", "%online%/%max%", "< ========== >"),
                        createDefault("STAINED_CLAY", 13, "< =========== >", "%name%", "%online%/%max%", "< =========== >")
                ), Arrays.asList(
                        createDefault("STAINED_CLAY", 4, "< = >", "%name%", "§6§lFULL", "< = >"),
                        createDefault("STAINED_CLAY", 4, "< == >", "%name%", "§6§lFULL", "< == >"),
                        createDefault("STAINED_CLAY", 4, "< === >", "%name%", "§6§lFULL", "< === >"),
                        createDefault("STAINED_CLAY", 4, "< ==== >", "%name%", "§6§lFULL", "< ==== >"),
                        createDefault("STAINED_CLAY", 4, "< ===== >", "%name%", "§6§lFULL", "< ===== >"),
                        createDefault("STAINED_CLAY", 4, "< ====== >", "%name%", "§6§lFULL", "< ====== >"),
                        createDefault("STAINED_CLAY", 4, "< ======= >", "%name%", "§6§lFULL", "< ======= >"),
                        createDefault("STAINED_CLAY", 4, "< ======== >", "%name%", "§6§lFULL", "< ======== >"),
                        createDefault("STAINED_CLAY", 4, "< ========= >", "%name%", "§6§lFULL", "< ========= >"),
                        createDefault("STAINED_CLAY", 4, "< ========== >", "%name%", "§6§lFULL", "< ========== >"),
                        createDefault("STAINED_CLAY", 4, "< =========== >", "%name%", "§6§lFULL", "< =========== >")
                ), Arrays.asList(
                        createDefault("STAINED_CLAY", 14, "< = >", "%name%", "§4§lMAINTENANCE", "< = >"),
                        createDefault("STAINED_CLAY", 14, "< == >", "%name%", "§4§lMAINTENANCE", "< == >"),
                        createDefault("STAINED_CLAY", 14, "< === >", "%name%", "§4§lMAINTENANCE", "< === >"),
                        createDefault("STAINED_CLAY", 14, "< ==== >", "%name%", "§4§lMAINTENANCE", "< ==== >"),
                        createDefault("STAINED_CLAY", 14, "< ===== >", "%name%", "§4§lMAINTENANCE", "< ===== >"),
                        createDefault("STAINED_CLAY", 14, "< ====== >", "%name%", "§4§lMAINTENANCE", "< ====== >"),
                        createDefault("STAINED_CLAY", 14, "< ======= >", "%name%", "§4§lMAINTENANCE", "< ======= >"),
                        createDefault("STAINED_CLAY", 14, "< ======== >", "%name%", "§4§lMAINTENANCE", "< ======== >"),
                        createDefault("STAINED_CLAY", 14, "< ========= >", "%name%", "§4§lMAINTENANCE", "< ========= >"),
                        createDefault("STAINED_CLAY", 14, "< ========== >", "%name%", "§4§lMAINTENANCE", "< ========== >"),
                        createDefault("STAINED_CLAY", 14, "< =========== >", "%name%", "§4§lMAINTENANCE", "< =========== >")
                ));
    }

    private static SignSubLayout createDefault(String block, int subID, String... lines) {
        return new SignSubLayout(lines, block, subID);
    }
}
