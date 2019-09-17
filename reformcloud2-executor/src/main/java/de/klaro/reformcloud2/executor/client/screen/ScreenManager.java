package de.klaro.reformcloud2.executor.client.screen;

import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.client.ClientExecutor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class ScreenManager extends AbsoluteThread {

    public ScreenManager() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    private final byte[] buffer = new byte[1024];

    private final StringBuffer stringBuffer = new StringBuffer();

    private final Map<UUID, ProcessScreen> perProcessScreenLines = new ConcurrentHashMap<>();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            ClientExecutor.getInstance().getProcessManager().getAll().forEach(runningProcess -> {
                readLog(runningProcess);
                readErrorLog(runningProcess);
            });
            AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 200);
        }
    }

    private synchronized void readLog(RunningProcess runningProcess) {
        if (!runningProcess.getProcess().isPresent()
                || !runningProcess.running()
                || runningProcess.getProcess().get().getInputStream() == null) {
            return;
        }

        InputStream inputStream = runningProcess.getProcess().get().getInputStream();
        doRead(inputStream, runningProcess);
    }

    private synchronized void readErrorLog(RunningProcess runningProcess) {
        if (!runningProcess.getProcess().isPresent()
                || !runningProcess.running()
                || runningProcess.getProcess().get().getInputStream() == null) {
            return;
        }

        InputStream errorStream = runningProcess.getProcess().get().getErrorStream();
        doRead(errorStream, runningProcess);
    }

    private void doRead(InputStream inputStream, RunningProcess runningProcess) {
        try {
            int len;
            while (inputStream.available() > 0 && (len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                stringBuffer.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
            }

            String text = stringBuffer.toString();
            if (!text.contains("\n") && !text.contains("\r")) {
                stringBuffer.setLength(0);
                return;
            }

            ProcessScreen screen = perProcessScreenLines.get(runningProcess.getProcessInformation().getProcessUniqueID());

            for (String in : text.split("\r")) {
                for (String string : in.split("\n")) {
                    if (!text.trim().isEmpty()) {
                        screen.addScreenLine(string);
                    }
                }
            }

            stringBuffer.setLength(0);
        } catch (final Throwable ignored) {
            stringBuffer.setLength(0);
        }
    }

    public Map<UUID, ProcessScreen> getPerProcessScreenLines() {
        return perProcessScreenLines;
    }
}
