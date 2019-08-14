package de.klaro.reformcloud2.executor;

import de.klaro.reformcloud2.executor.api.ExecutorType;

public final class ExecutorChooser {

    public static synchronized void main(String[] args) {
        ExecutorType executor = ExecutorType.getByID(toID(System.getProperty("reformcloud.executor.type", "-1")));
        if (!executor.isSupported()) {
            throw new RuntimeException("Unsupported executor used!");
        }
    }

    private static Integer toID(String convert) {
        try {
            return Integer.parseInt(convert);
        } catch (final Throwable throwable) {
            //May cause if system variable is not set properly
            return -1;
        }
    }
}
