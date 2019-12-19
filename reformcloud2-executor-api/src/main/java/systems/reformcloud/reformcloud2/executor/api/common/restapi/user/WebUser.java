package systems.reformcloud.reformcloud2.executor.api.common.restapi.user;

import com.google.gson.reflect.TypeToken;
import java.util.Collection;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public class WebUser implements Nameable {

  public static final TypeToken<WebUser> TYPE = new TypeToken<WebUser>() {};

  public WebUser(String name, String token, Collection<String> permissions) {
    this.name = name;
    this.token = token;
    this.permissions = permissions;
  }

  private final String name;

  private final String token;

  private final Collection<String> permissions;

  @Nonnull
  @Override
  public String getName() {
    return name;
  }

  public String getToken() { return token; }

  public Collection<String> getPermissions() { return permissions; }
}
