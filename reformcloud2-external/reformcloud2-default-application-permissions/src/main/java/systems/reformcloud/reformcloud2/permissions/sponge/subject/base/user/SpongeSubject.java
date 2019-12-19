package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.user;

import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.impl.AbstractUserSpongeSubject;

public class SpongeSubject extends AbstractUserSpongeSubject {

  public SpongeSubject(@Nonnull UUID user, @Nonnull SubjectCollection source,
                       @Nonnull PermissionService service) {
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
    return PermissionAPI.getInstance()
        .getPermissionUtil()
        .loadUser(uniqueUserID)
        .hasPermission(permission);
  }

  @Override
  @Nonnull
  public SubjectCollection getContainingCollection() {
    return this.source;
  }

  @Override
  @Nonnull
  public String getIdentifier() {
    return this.uniqueUserID.toString();
  }
}
