package systems.reformcloud.reformcloud2.executor.api.common.dependency.repo;

import javax.annotation.Nonnull;

public final class DefaultRepositories {

  public static final Repository MAVEN_CENTRAL = new Repository() {
    @Nonnull
    @Override
    public String getName() {
      return "Central";
    }

    @Nonnull
    @Override
    public String getURL() {
      return "https://repo.maven.apache.org/maven2/";
    }
  };

  public static final Repository SONATYPE = new Repository() {
    @Nonnull
    @Override
    public String getName() {
      return "SonaType";
    }

    @Nonnull
    @Override
    public String getURL() {
      return "https://oss.sonatype.org/content/repositories/releases/";
    }
  };

  public static final Repository J_CENTER = new Repository() {
    @Nonnull
    @Override
    public String getName() {
      return "JCenter";
    }

    @Nonnull
    @Override
    public String getURL() {
      return "http://jcenter.bintray.com/";
    }
  };

  private DefaultRepositories() { throw new UnsupportedOperationException(); }
}
