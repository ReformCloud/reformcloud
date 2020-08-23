/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.signs.application;

import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignLayout;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignSubLayout;

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
                createDefault("STAINED_CLAY", 2, "< = >", "%name%", " Connecting ", "< = >"),
                createDefault("STAINED_CLAY", 2, "< == >", "%name%", " Connecting. ", "< == >"),
                createDefault("STAINED_CLAY", 2, "< === >", "%name%", " Connecting.. ", "< === >"),
                createDefault("STAINED_CLAY", 2, "< ==== >", "%name%", " Connecting... ", "< ==== >"),
                createDefault("STAINED_CLAY", 2, "< ===== >", "%name%", " Connecting.... ", "< ===== >"),
                createDefault("STAINED_CLAY", 2, "< ====== >", "%name%", " Connecting..... ", "< ====== >"),
                createDefault("STAINED_CLAY", 2, "< ======= >", "%name%", " Connecting.... ", "< ======= >"),
                createDefault("STAINED_CLAY", 2, "< ======== >", "%name%", " Connecting... ", "< ======== >"),
                createDefault("STAINED_CLAY", 2, "< ========= >", "%name%", " Connecting.. ", "< ========= >"),
                createDefault("STAINED_CLAY", 2, "< ========== >", "%name%", " Connecting. ", "< ========== >"),
                createDefault("STAINED_CLAY", 2, "< =========== >", "%name%", " Connecting ", "< =========== >")
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
