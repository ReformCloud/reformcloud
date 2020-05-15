/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.controller.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.process.JavaProcessHelper;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public final class ClientManager {

    public static final ClientManager INSTANCE = new ClientManager();
    private final Collection<ClientRuntimeInformation> clientRuntimeInformation = new CopyOnWriteArrayList<>();
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

    public ClientRuntimeInformation getClientInfo(@NotNull String name) {
        return Streams.filter(this.clientRuntimeInformation, e -> e.getName().equals(name));
    }

    public Collection<ClientRuntimeInformation> getClientRuntimeInformation() {
        return clientRuntimeInformation;
    }
}
