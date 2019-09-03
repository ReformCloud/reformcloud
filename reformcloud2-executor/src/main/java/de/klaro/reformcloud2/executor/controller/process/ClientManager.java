package de.klaro.reformcloud2.executor.controller.process;

import de.klaro.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class ClientManager {

    public final List<ClientRuntimeInformation> clientRuntimeInformation = new ArrayList<>();

    public static final ClientManager INSTANCE = new ClientManager();

    public void connectClient(ClientRuntimeInformation info) {
        clientRuntimeInformation.add(info);
    }

    public void disconnectClient(String name) {
        ClientRuntimeInformation found = Links.filter(clientRuntimeInformation, new Predicate<ClientRuntimeInformation>() {
            @Override
            public boolean test(ClientRuntimeInformation clientRuntimeInformation) {
                return clientRuntimeInformation.getName().equals(name);
            }
        });
        if (found == null) {
            return;
        }

        clientRuntimeInformation.remove(found);
    }
}
