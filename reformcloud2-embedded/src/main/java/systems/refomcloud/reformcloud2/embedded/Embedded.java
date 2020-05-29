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
package systems.refomcloud.reformcloud2.embedded;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

/**
 * This class can only get called if the environment is {@link systems.reformcloud.reformcloud2.executor.api.ExecutorType#API}.
 * Check this by using {@link ExecutorAPI#getType()}. If the current instance is not an api instance
 * just use the default cloud api based on {@link ExecutorAPI#getInstance()}.
 */
public abstract class Embedded extends ExecutorAPI {

    protected Embedded() {
        this.getEventManager().registerListener(this);
    }

    protected ProcessInformation processInformation;

    /**
     * @return The current api instance the cloud is running on
     */
    @NotNull
    public static Embedded getInstance() {
        return (Embedded) ExecutorAPI.getInstance();
    }

    /**
     * @return The current process information the current api instance is using
     */
    @NotNull
    public abstract ProcessInformation getCurrentProcessInformation();

    /**
     * Updates the current process information
     */
    public abstract void updateCurrentProcessInformation();

    @Listener
    public void handleProcessInfoUpdate(@NotNull ProcessUpdatedEvent event) {
        if (this.processInformation.getProcessDetail().getProcessUniqueID().equals(event.getProcessInformation().getProcessDetail().getProcessUniqueID())) {
            this.processInformation = event.getProcessInformation();
        }
    }
}
