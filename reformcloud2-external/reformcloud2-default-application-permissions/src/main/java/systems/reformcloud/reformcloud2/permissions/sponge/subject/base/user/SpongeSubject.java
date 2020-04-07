package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.user;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.impl.AbstractUserSpongeSubject;

import java.util.UUID;

public class SpongeSubject extends AbstractUserSpongeSubject {

    public SpongeSubject(@NotNull UUID user, @NotNull SubjectCollection source, @NotNull PermissionService service) {
        super(user);
        this.uniqueUserID = user;
        this.source = source;
        this.service = service;
    }

    private final UUID uniqueUserID;

    private final SubjectCollection source;

    private final PermissionService service;

    @Override
    protected PermissionService service() {
        return this.service;
    }

    @Override
    protected boolean has(String permission) {
        return PermissionAPI.getInstance().getPermissionUtil().loadUser(uniqueUserID).hasPermission(permission);
    }

    @Override
    @NotNull
    public SubjectCollection getContainingCollection() {
        return this.source;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return this.uniqueUserID.toString();
    }
}
