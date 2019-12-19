package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.system;

import java.util.Optional;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.impl.AbstractSystemSubject;

public class SystemSubject extends AbstractSystemSubject {

  public SystemSubject(@Nonnull String id, @Nonnull PermissionService service,
                       @Nonnull SubjectCollection source) {
    super(new SystemSubjectData());
    this.id = id;
    this.service = service;
    this.source = source;
  }

  private final String id;

  private final PermissionService service;

  private final SubjectCollection source;

  @Nonnull
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
  @Nonnull
  public SubjectCollection getContainingCollection() {
    return this.source;
  }

  @Override
  @Nonnull
  public String getIdentifier() {
    return this.id;
  }
}
