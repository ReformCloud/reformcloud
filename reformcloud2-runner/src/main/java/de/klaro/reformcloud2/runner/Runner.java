package de.klaro.reformcloud2.runner;

import java.io.Console;
import java.util.function.Predicate;

public final class Runner {

    private static final Predicate<String> CONTROLLER_UNPACK_TEST = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return s != null && (s.equalsIgnoreCase("controller") || s.equalsIgnoreCase("client"));
        }
    };

    private static final Runnable CHOOSE_INSTALL_MESSAGE = new Runnable() {
        @Override
        public void run() {
            System.out.println("Please choose an executor: [\"controller\", \"client\"]");
        }
    };

    /* ================================== */

    public static synchronized void main(String[] args) {
        if (isNotAPI()) {
            if (shouldUnpackController()) {

            } else {

            }
        } else {

        }
    }

    private static boolean isNotAPI() {
        return System.getProperty("reformcloud.executor.type") != null &&
                !System.getProperty("reformcloud.executor.type").equals("3");
    }

    private static boolean shouldUnpackController() {
        CHOOSE_INSTALL_MESSAGE.run();

        Console console = System.console();
        String s = console.readLine();
        while (s == null || s.trim().isEmpty() || !CONTROLLER_UNPACK_TEST.test(s)) {
            CHOOSE_INSTALL_MESSAGE.run();
            s = console.readLine();
        }

        return s.equalsIgnoreCase("controller");
    }
}
