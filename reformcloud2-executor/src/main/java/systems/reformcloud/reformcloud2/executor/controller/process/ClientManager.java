package systems.reformcloud.reformcloud2.executor.controller.process;

import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.process.JavaProcessHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public final class ClientManager {

    public static final ClientManager INSTANCE = new ClientManager();
    private final Collection<ClientRuntimeInformation> clientRuntimeInformation = new ArrayList<>();
    /**
     * Represents the internal client process
     */
    private Process process;

    public void connectClient(ClientRuntimeInformation info) {
        clientRuntimeInformation.add(info);
    }

    public void disconnectClient(String name) {
        ClientRuntimeInformation found = Streams.filter(clientRuntimeInformation, clientRuntimeInformation -> clientRuntimeInformation.getName().equals(name));
        if (found == null) {
            return;
        }

        clientRuntimeInformation.remove(found);
        System.out.println(LanguageManager.get(
                "client-connection-lost",
                found.getName()
        ));
    }

    public void updateClient(ClientRuntimeInformation information) {
        ClientRuntimeInformation found = Streams.filter(clientRuntimeInformation, clientRuntimeInformation -> clientRuntimeInformation.getName().equals(information.getName()));
        if (found == null) {
            return;
        }

        clientRuntimeInformation.remove(found);
        clientRuntimeInformation.add(information);
    }

    public void onShutdown() {
        clientRuntimeInformation.clear();
        if (process == null) {
            return;
        }

        JavaProcessHelper.shutdown(process, true, true, TimeUnit.SECONDS.toMillis(10), "stop\n");
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Collection<ClientRuntimeInformation> getClientRuntimeInformation() {
        return clientRuntimeInformation;
    }
}
