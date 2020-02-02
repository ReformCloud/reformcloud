package systems.reformcloud.reformcloud2.executor.node.process.log;

import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class LogLineReader extends AbsoluteThread {

    public LogLineReader() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    private final StringBuffer stringBuffer = new StringBuffer();

    private final byte[] buffer = new byte[1024];

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Streams.newList(LocalProcessManager.getNodeProcesses()).stream().filter(e -> e.getProcess().isPresent()).forEach(e -> {
                InputStream inputStream = e.getProcess().get().getInputStream();
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

                    NodeProcessScreenHandler.getScreen(e.getProcessInformation().getProcessUniqueID()).ifPresent(s -> {
                        for (String in : text.split("\r")) {
                            for (String string : in.split("\n")) {
                                if (!string.trim().isEmpty()) {
                                    s.addScreenLine(string);
                                }
                            }
                        }
                    });

                    stringBuffer.setLength(0);
                } catch (final Throwable ignored) {
                    stringBuffer.setLength(0);
                }
            });

            AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 50);
        }
    }
}
