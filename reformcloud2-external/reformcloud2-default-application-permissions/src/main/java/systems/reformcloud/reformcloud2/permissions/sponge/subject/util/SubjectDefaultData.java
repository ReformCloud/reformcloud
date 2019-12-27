package systems.reformcloud.reformcloud2.permissions.sponge.subject.util;

import org.spongepowered.api.service.permission.Subject;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.CollectionCatalog;
import systems.reformcloud.reformcloud2.permissions.sponge.service.SpongePermissionService;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.user.SpongeSubject;

import java.util.UUID;

public final class SubjectDefaultData {

    private SubjectDefaultData() {
        throw new UnsupportedOperationException();
    }

    private static final UUID DEFAULT_UUID = UUID.fromString("a038e8a0-d294-45a5-86f0-bf44d316beec");

    public static final Subject DEFAULT = new SpongeSubject(DEFAULT_UUID, CollectionCatalog.USER_COLLECTION, SpongePermissionService.getInstance());
}
