package systems.reformcloud.reformcloud2.executor.client.process.basic;

import java.util.*;
import systems.reformcloud.reformcloud2.executor.api.client.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.api.client.process.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.packet.out.ClientPacketOutProcessRegistered;
import systems.reformcloud.reformcloud2.executor.client.screen.ProcessScreen;

public final class DefaultProcessManager implements ProcessManager {

  private final List<RunningProcess> list = new ArrayList<>();

  @Override
  public void registerProcess(RunningProcess runningProcess) {
    list.add(runningProcess);
    ClientExecutor.getInstance()
        .getScreenManager()
        .getPerProcessScreenLines()
        .put(runningProcess.getProcessInformation().getProcessUniqueID(),
             new ProcessScreen(
                 runningProcess.getProcessInformation().getProcessUniqueID()));

    DefaultChannelManager.INSTANCE.get("Controller")
        .ifPresent(
            packetSender
            -> packetSender.sendPacket(new ClientPacketOutProcessRegistered(
                runningProcess.getProcessInformation().getProcessUniqueID(),
                runningProcess.getProcessInformation().getName())));
  }

  @Override
  public void unregisterProcess(String name) {
    Links
        .filterToReference(
            list,
            runningProcess
            -> runningProcess.getProcessInformation().getName().equals(name))
        .ifPresent(runningProcess -> {
          list.remove(runningProcess);
          ClientExecutor.getInstance()
              .getScreenManager()
              .getPerProcessScreenLines()
              .remove(
                  runningProcess.getProcessInformation().getProcessUniqueID());
        });
  }

  @Override
  public ReferencedOptional<RunningProcess> getProcess(UUID uniqueID) {
    return Links.filterToReference(
        list,
        runningProcess
        -> runningProcess.getProcessInformation().getProcessUniqueID().equals(
            uniqueID));
  }

  @Override
  public ReferencedOptional<RunningProcess> getProcess(String name) {
    return Links.filterToReference(
        list,
        runningProcess
        -> runningProcess.getProcessInformation().getName().equals(name));
  }

  @Override
  public Collection<RunningProcess> getAll() {
    return Collections.unmodifiableCollection(list);
  }

  @Override
  public void onProcessDisconnect(UUID uuid) {
    Links
        .filterToReference(list,
                           runningProcess
                           -> runningProcess.getProcessInformation()
                                  .getProcessUniqueID()
                                  .equals(uuid))
        .ifPresent(RunningProcess::shutdown);
  }

  @Override
  public void stopAll() {
    Links.newList(list).forEach(RunningProcess::shutdown);
    list.clear();
  }
}
