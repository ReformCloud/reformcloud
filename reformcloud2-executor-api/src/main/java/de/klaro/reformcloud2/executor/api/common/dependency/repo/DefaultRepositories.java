package de.klaro.reformcloud2.executor.api.common.dependency.repo;

public final class DefaultRepositories {

    public static final Repository MAVEN_CENTRAL = new Repository() {
        @Override
        public String getName() {
            return "Central";
        }

        @Override
        public String getURL() {
            return "https://repo.maven.apache.org/maven2/";
        }
    };

    public static final Repository SONATYPE = new Repository() {
        @Override
        public String getName() {
            return "SonaType";
        }

        @Override
        public String getURL() {
            return "https://oss.sonatype.org/content/repositories/releases/";
        }
    };

    public static final Repository J_CENTER = new Repository() {
        @Override
        public String getName() {
            return "JCenter";
        }

        @Override
        public String getURL() {
            return "http://jcenter.bintray.com/";
        }
    };

    private DefaultRepositories() {}
}
