package de.klaro.reformcloud2.executor.controller.process;

import de.klaro.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public final class ClientManager {

    private final Collection<ClientRuntimeInformation> clientRuntimeInformation = new ArrayList<>();

    /**
     * Represents the internal client process
     */
    private Process process;

    public static final ClientManager INSTANCE = new ClientManager();

    public void connectClient(ClientRuntimeInformation info) {
        clientRuntimeInformation.add(info);
    }

    public void disconnectClient(String name) {
        ClientRuntimeInformation found = Links.filter(clientRuntimeInformation, clientRuntimeInformation -> clientRuntimeInformation.getName().equals(name));
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
        ClientRuntimeInformation found = Links.filter(clientRuntimeInformation, clientRuntimeInformation -> clientRuntimeInformation.getName().equals(information.getName()));
        if (found == null) {
            return;
        }

        clientRuntimeInformation.remove(found);
        clientRuntimeInformation.add(information);
    }

    public void onShutdown() {
        clientRuntimeInformation.clear();
        if (process != null) {
            try {
                process.getOutputStream().write("stop".getBytes());
                process.getOutputStream().flush();
            } catch (final IOException ignored) {
            }

            process.destroyForcibly().destroy();
        }
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
