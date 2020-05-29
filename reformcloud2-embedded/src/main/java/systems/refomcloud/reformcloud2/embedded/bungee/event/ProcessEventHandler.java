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
package systems.refomcloud.reformcloud2.embedded.bungee.event;

import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.bungee.BungeeExecutor;
import systems.reformcloud.reformcloud2.executor.api.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;

public final class ProcessEventHandler {

    @Listener
    public void handleStart(final @NotNull ProcessStartedEvent event) {
        BungeeExecutor.registerServer(event.getProcessInformation());
    }

    @Listener
    public void handleUpdate(final @NotNull ProcessUpdatedEvent event) {
        BungeeExecutor.registerServer(event.getProcessInformation());
    }

    @Listener
    public void handleRemove(final @NotNull ProcessStoppedEvent event) {
        BungeeExecutor.unregisterServer(event.getProcessInformation());
    }
}
