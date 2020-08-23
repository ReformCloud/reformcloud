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
package systems.reformcloud.reformcloud2.examples.process;

import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;

import java.util.Optional;

public class ExampleProcessHandling {

    // Starts a new process of the lobby group
    public static void startProcessFromGroup() {
        ExecutorAPI.getInstance().getProcessProvider().createProcess() // get a new process builder
                .group("Lobby") // Use the group lobby. REQUIRED
                .prepare() // Prepare the process
                .thenAccept(wrapper -> wrapper.setRuntimeState(ProcessState.STARTED)); // start the process
    }

    // Starts a new process of the group lobby with the template default
    public static void startProcessFromGroupWithTemplate() {
        Optional<ProcessGroup> group = ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup("Lobby");
        ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup("Lobby")
                .ifPresent(processGroup -> {
                    Template template = processGroup.getTemplate("default");
                    if (template == null) {
                        return;
                    }

                    ExecutorAPI.getInstance().getProcessProvider().createProcess()
                            .group("Lobby") // Use the group lobby. REQUIRED
                            .template(template) // Set the template to 'default'
                            .prepare() // Prepare the process
                            .thenAccept(wrapper -> wrapper.setRuntimeState(ProcessState.STARTED)); // start the process
                });
    }
}
