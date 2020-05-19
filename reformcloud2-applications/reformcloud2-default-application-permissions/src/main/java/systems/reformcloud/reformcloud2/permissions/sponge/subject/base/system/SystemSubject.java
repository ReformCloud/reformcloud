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
package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.system;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.impl.AbstractSystemSubject;

import java.util.Optional;

public class SystemSubject extends AbstractSystemSubject {

    private final String id;
    private final PermissionService service;
    private final SubjectCollection source;

    public SystemSubject(@NotNull String id, @NotNull PermissionService service, @NotNull SubjectCollection source) {
        super(new SystemSubjectData());
        this.id = id;
        this.service = service;
        this.source = source;
    }

    @NotNull
    @Override
    public Optional<CommandSource> getCommandSource() {
        if (this.id.equals(PermissionService.SUBJECTS_SYSTEM)) {
            return Sponge.getServer().getConsole().getCommandSource();
        }

        return Optional.empty();
    }

    @Override
    protected PermissionService service() {
        return this.service;
    }

    @Override
    protected boolean has(String permission) {
        return true;
    }

    @Override
    @NotNull
    public SubjectCollection getContainingCollection() {
        return this.source;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return this.id;
    }
}
