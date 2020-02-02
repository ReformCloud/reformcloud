package systems.reformcloud.reformcloud2.executor.client.watchdog;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.packet.out.ClientPacketOutProcessWatchdogStopped;

import java.util.concurrent.TimeUnit;

public final class WatchdogThread extends AbsoluteThread {

    public WatchdogThread() {
        updatePriority(Thread.MIN_PRIORITY).enableDaemon().start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Streams.newList(ClientExecutor.getInstance().getProcessManager().getAll()).forEach(runningProcess -> {
                if (!runningProcess.running()
                        && runningProcess.getStartupTime() != -1
                        && runningProcess.getStartupTime() + TimeUnit.SECONDS.toMillis(30) < System.currentTimeMillis()) {
                    runningProcess.shutdown();
                    DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketOutProcessWatchdogStopped(runningProcess.getProcessInformation().getName())));
                }
            });

            AbsoluteThread.sleep(TimeUnit.SECONDS, 1);
        }
    }
}
