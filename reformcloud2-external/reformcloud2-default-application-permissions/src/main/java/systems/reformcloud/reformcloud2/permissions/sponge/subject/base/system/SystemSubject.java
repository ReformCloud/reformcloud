package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.system;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.impl.AbstractSystemSubject;

import java.util.Optional;

public class SystemSubject extends AbstractSystemSubject {

    public SystemSubject(@NotNull String id, @NotNull PermissionService service, @NotNull SubjectCollection source) {
        super(new SystemSubjectData());
        this.id = id;
        this.service = service;
        this.source = source;
    }

    private final String id;

    private final PermissionService service;

    private final SubjectCollection source;

    @NotNull
    @Override
    public Optional<CommandSource> getCommandSource() {
        if (id.equals(PermissionService.SUBJECTS_SYSTEM)) {
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
