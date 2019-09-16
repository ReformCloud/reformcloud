package de.klaro.reformcloud2.executor.client.watchdog;

import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.client.ClientExecutor;
import de.klaro.reformcloud2.executor.client.packet.out.ClientPacketOutProcessWatchdogStopped;

import java.util.concurrent.TimeUnit;

public final class WatchdogThread extends AbsoluteThread {

    public WatchdogThread() {
        updatePriority(Thread.MIN_PRIORITY).enableDaemon().start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Links.newList(ClientExecutor.getInstance().getProcessManager().getAll()).forEach(runningProcess -> {
                if (!runningProcess.running()
                        && runningProcess.getStartupTime() != -1
                        && runningProcess.getStartupTime() + TimeUnit.SECONDS.toMillis(2) < System.currentTimeMillis()) {
                    runningProcess.shutdown();
                    DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketOutProcessWatchdogStopped(runningProcess.getProcessInformation().getName())));
                }
            });

            AbsoluteThread.sleep(TimeUnit.SECONDS, 1);
        }
    }
}
