package de.klaro.reformcloud2.runner;

import de.klaro.reformcloud2.runner.classloading.ClassPreparer;
import de.klaro.reformcloud2.runner.classloading.RunnerClassLoader;
import de.klaro.reformcloud2.runner.util.ExceptionFunction;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarFile;

public final class Runner {

    static {
        System.setProperty(
                "reformcloud.runner.version",
                Runner.class.getPackage().getImplementationVersion()
        );

        System.setProperty(
                "reformcloud.runner.specification",
                Runner.class.getPackage().getSpecificationVersion()
        );
    }

    private static final Predicate<String> CONTROLLER_UNPACK_TEST = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return s != null && (s.equalsIgnoreCase("controller") || s.equalsIgnoreCase("client"));
        }
    };

    private static final Runnable CHOOSE_INSTALL_MESSAGE = new Runnable() {
        @Override
        public void run() {
            System.out.println("Please choose an executor: [\"controller\", \"client\"]");
        }
    };

    /* ================================== */

    public static synchronized void main(String[] args) {
        if (isNotAPI()) {
            startSetup(new BiConsumer<String, String>() {
                @Override
                public void accept(String version, String id) {
                    final File file = new File("reformcloud/.bin/executor.jar");
                    if (!file.exists()) {
                        throw new RuntimeException("Executor file does not exists");
                    }

                    ClassLoader classLoader = ClassPreparer.create(file.toPath(),
                            new ExceptionFunction<Path, ClassLoader>() {
                                @Override
                                public ClassLoader apply(Path path) throws Exception {
                                    URL[] urls = new URL[]{path.toUri().toURL()};
                                    return new RunnerClassLoader(urls);
                                }
                            });
                    if (!(classLoader instanceof URLClassLoader)) {
                        throw new RuntimeException("ClassLoader has to be a url class loader");
                    }

                    updateClassLoader(classLoader);

                    try (JarFile jarFile = new JarFile(file)) {
                        String main = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
                        Method invoke = classLoader.loadClass(main).getMethod("main", String[].class);
                        invoke.setAccessible(true);

                        switch (id) {
                            case "1": {
                                createInvoke("1");
                                invoke.invoke(null, (Object) args);
                                break;
                            }

                            case "2": {
                                createInvoke("2");
                                invoke.invoke(null, (Object) args);
                                break;
                            }

                            default: {
                                throw new RuntimeException("Unknown id provided by config detected");
                            }
                        }
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else {
            runIfProcessExists(new Consumer<Path>() {
                @Override
                public void accept(Path path) {
                    ClassLoader classLoader = ClassPreparer.create(path, new ExceptionFunction<Path, ClassLoader>() {
                        @Override
                        public ClassLoader apply(Path path) throws Exception {
                            URL[] urls = new URL[]{path.toUri().toURL()};
                            return new RunnerClassLoader(urls);
                        }
                    });
                    if (!(classLoader instanceof URLClassLoader)) {
                        throw new RuntimeException("ClassLoader has to be a url class loader");
                    }

                    updateClassLoader(classLoader);
                    try (JarFile jarFile = new JarFile(path.toFile())) {
                        String main = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
                        Method invoke = classLoader.loadClass(main).getMethod("main", String[].class);
                        invoke.setAccessible(true);
                        invoke.invoke(null, (Object) args);
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    private static void startSetup(BiConsumer<String, String> andThen) {
        unpackExecutor();

        Properties properties = new Properties();
        if (Files.exists(Paths.get("reformcloud/.bin/config.properties"))) {
            try (InputStream inputStream = Files.newInputStream(Paths.get("reformcloud/.bin/config.properties"))) {
                properties.load(inputStream);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }

            andThen.accept(
                    properties.getProperty("reformcloud.version"),
                    properties.getProperty("reformcloud.type.id")
            );
            return;
        }

        if (shouldUnpackController()) {
            andThen.accept(write(properties, "1"), "1");
        } else {
            andThen.accept(write(properties, "2"), "2");
        }
    }

    private static boolean isNotAPI() {
        return System.getProperty("reformcloud.executor.type") != null &&
                !System.getProperty("reformcloud.executor.type").equals("3");
    }

    private static String write(Properties properties, String id) {
        String version = System.getProperty("reformcloud.runner.version");

        properties.setProperty("reformcloud.version", version);
        properties.setProperty("reformcloud.type.id", id);

        try (OutputStream outputStream = Files.newOutputStream(Paths.get("reformcloud/.bin/config.properties"))) {
            properties.store(outputStream, "ReformCloud runner configuration");
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return version;
    }

    private static boolean shouldUnpackController() {
        CHOOSE_INSTALL_MESSAGE.run();

        Console console = System.console();
        String s = console.readLine();
        while (s == null || s.trim().isEmpty() || !CONTROLLER_UNPACK_TEST.test(s)) {
            CHOOSE_INSTALL_MESSAGE.run();
            s = console.readLine();
        }

        return s.equalsIgnoreCase("controller");
    }

    private static void unpackExecutor() {
        try (InputStream inputStream = Runner.class.getClassLoader().getResourceAsStream("internal/files/executor.jar")) {
            Files.createDirectories(Paths.get("reformcloud/.bin/libs"));
            Files.copy(Objects.requireNonNull(inputStream), Paths.get("reformcloud/.bin/executor.jar"), StandardCopyOption.REPLACE_EXISTING);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void updateClassLoader(ClassLoader newLoader) {
        Thread.currentThread().setContextClassLoader(newLoader);

        try {
            Field field = ClassLoader.class.getDeclaredField("scl");
            field.setAccessible(true);
            field.set(null, newLoader);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createInvoke(String id) {
        System.setProperty("reformcloud.executor.type", id);
    }

    /* ================== */

    private static void runIfProcessExists(Consumer<Path> consumer) {
        if (!Files.exists(Paths.get("process.jar"))) {
            throw new RuntimeException("Cannot find process jar to execute");
        }

        consumer.accept(Paths.get("process.jar"));
    }
}
