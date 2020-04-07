package systems.reformcloud.reformcloud2.executor.api.common.dependency.repo;

import org.jetbrains.annotations.NotNull;

public final class DefaultRepositories {

    public static final Repository MAVEN_CENTRAL = new Repository() {
        @NotNull
        @Override
        public String getName() {
            return "Central";
        }

        @NotNull
        @Override
        public String getURL() {
            return "https://repo.maven.apache.org/maven2/";
        }
    };

    public static final Repository REFORMCLOUD = new Repository() {
        @NotNull
        @Override
        public String getName() {
            return "ReformCloud-Central";
        }

        @NotNull
        @Override
        public String getURL() {
            return "https://repo.reformcloud.systems/";
        }
    };

    public static final Repository SONATYPE = new Repository() {
        @NotNull
        @Override
        public String getName() {
            return "SonaType";
        }

        @NotNull
        @Override
        public String getURL() {
            return "https://oss.sonatype.org/content/repositories/releases/";
        }
    };

    public static final Repository J_CENTER = new Repository() {
        @NotNull
        @Override
        public String getName() {
            return "JCenter";
        }

        @NotNull
        @Override
        public String getURL() {
            return "http://jcenter.bintray.com/";
        }
    };

    private DefaultRepositories() {
        throw new UnsupportedOperationException();
    }
}
