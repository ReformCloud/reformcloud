package systems.reformcloud.reformcloud2.executor.api.common.application;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.io.File;
import java.util.jar.JarEntry;

public interface ApplicationConfig extends Nameable {

    String version();

    String author();

    String main();

    Dependency[] dependencies();

    String description();

    String website();

    File applicationFile();

    JarEntry applicationConfigFile();
}
