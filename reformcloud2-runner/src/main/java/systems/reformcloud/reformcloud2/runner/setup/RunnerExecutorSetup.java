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
package systems.reformcloud.reformcloud2.runner.setup;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.io.Console;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class RunnerExecutorSetup {

    private RunnerExecutorSetup() {
        throw new UnsupportedOperationException();
    }

    /**
     * Executes the runner executor setup
     */
    public static void executeSetup() {
        System.out.println("Please choose an executor: \"node\" (recommended), \"controller\", \"client\"");
        System.out.println("!! Please note that you should use the recommended installation because the controller/client system is deprecated and will be removed in a further release !!");
        System.out.println("For more information check out the README on GitHub: " + RunnerUtils.REPO_BASE_URL);

        String executor = readFromConsoleOrFromSystemProperties(
                s -> RunnerUtils.AVAILABLE_EXECUTORS.contains(s.toLowerCase()),
                s -> {
                    System.out.println("The executor " + s + " is not available.");
                    System.out.println("Please choose one of these: " + String.join(", ", RunnerUtils.AVAILABLE_EXECUTORS));
                });
        System.setProperty(
                "reformcloud.executor.type",
                Integer.toString(getIDFromType(executor))
        );
    }

    @NotNull
    private static String readFromConsoleOrFromSystemProperties(@NotNull Predicate<String> predicate,
                                                                @NotNull Consumer<String> wrongInput) {
        String property = System.getProperty("reformcloud.executor.type");
        if (property != null && predicate.test(property)) {
            return property;
        }

        Console console = System.console();
        String s = console.readLine();
        while (s == null || s.trim().isEmpty() || !predicate.test(s)) {
            wrongInput.accept(s);
            s = console.readLine();
        }

        return s;
    }

    private static int getIDFromType(@NotNull String type) {
        if (type.equalsIgnoreCase("node")) {
            return 4;
        }

        return type.equalsIgnoreCase("controller") ? 1 : 2;
    }

}
