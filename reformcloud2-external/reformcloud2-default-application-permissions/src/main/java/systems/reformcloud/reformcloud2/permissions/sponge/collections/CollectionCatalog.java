package systems.reformcloud.reformcloud2.permissions.sponge.collections;

import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.base.GroupSubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.base.SystemSubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.base.UserCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.factory.FactoryCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.service.SpongePermissionService;

public class CollectionCatalog {

    private CollectionCatalog() {
        throw new UnsupportedOperationException();
    }

    public static final SubjectCollection FACTORY_COLLECTION = new FactoryCollection(SpongePermissionService.getInstance());

    public static final SubjectCollection GROUP_COLLECTION = new GroupSubjectCollection(SpongePermissionService.getInstance());

    public static final SubjectCollection USER_COLLECTION = new UserCollection(SpongePermissionService.getInstance());

    public static final SubjectCollection COMMAND_BLOCK_COLLECTION = new SystemSubjectCollection(PermissionService.SUBJECTS_COMMAND_BLOCK, SpongePermissionService.getInstance());

    public static final SubjectCollection SYSTEM_COLLECTION = new SystemSubjectCollection(PermissionService.SUBJECTS_SYSTEM, SpongePermissionService.getInstance());
}
