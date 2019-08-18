package de.klaro.reformcloud2.executor.api.common.application;

import de.klaro.reformcloud2.executor.api.common.dependency.Dependency;
import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

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
