package systems.reformcloud.reformcloud2.runner;

import systems.reformcloud.reformcloud2.runner.classloading.ClassPreparer;
import systems.reformcloud.reformcloud2.runner.classloading.RunnerClassLoader;
import systems.reformcloud.reformcloud2.runner.update.ApplicationUpdateApplier;
import systems.reformcloud.reformcloud2.runner.update.CloudVersionUpdater;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
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
import java.util.function.Supplier;
import java.util.jar.JarFile;

public final class Runner {

    private static final Predicate<String> CONTROLLER_UNPACK_TEST = s -> s != null && (s.equalsIgnoreCase("controller") || s.equalsIgnoreCase("client") || s.equalsIgnoreCase("node"));

    private static final Supplier<String> CHOOSE_INSTALL_MESSAGE = () -> "Please choose an executor: [\"controller\", \"client\", \"node\" (recommended)]";

    private static final Properties PROPERTIES = new Properties();

    /* ================================== */

    public static synchronized void main(String[] args) {
        if (!isAPI()) {
            readProperties();
            setSystemProperties(Runner.class.getPackage().getImplementationVersion());

            String newVersion = null;
            if (System.getProperty("reformcloud.runner.version").endsWith("-SNAPSHOT")) {
                System.out.println("You are running on a snapshot build ("
                        + System.getProperty("reformcloud.runner.version") + "). Please note that this is " +
                        "not supported and no updates of the cloud will get applied.");
            } else if (Boolean.getBoolean("reformcloud.disable.updates")) {
                System.out.println("You've disabled auto update for applications and the cloud itself");
            } else {
                ApplicationUpdateApplier.applyUpdates();
                newVersion = CloudVersionUpdater.update();
            }

            startSetup((version, id) -> {
                final File file = new File("reformcloud/.bin/executor.jar");
                if (!file.exists()) {
                    throw new RuntimeException("Executor file does not exists");
                }

                setSystemProperties(Runner.class.getPackage().getImplementationVersion());

                ClassLoader classLoader = ClassPreparer.create(file.toPath(),
                        path -> {
                            URL[] urls = new URL[]{path.toUri().toURL()};
                            return new RunnerClassLoader(urls);
                        });
                if (!(classLoader instanceof URLClassLoader)) {
                    throw new RuntimeException("ClassLoader has to be an url class loader");
                }

                try (JarFile jarFile = new JarFile(file)) {
                    String main = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
                    Method invoke = classLoader.loadClass(main).getMethod("main", String[].class);

                    if (id.equals("1") || id.equals("2") || id.equals("4")) {
                        createInvoke(id);
                    } else {
                        throw new RuntimeException("Unknown id provided by config detected");
                    }

                    invokeMethod(invoke, args, classLoader);
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }, newVersion);
        } else {
            runIfProcessExists(path -> {
                ClassLoader classLoader = ClassPreparer.create(path, path1 -> {
                    URL[] urls = new URL[]{path1.toUri().toURL()};
                    return new RunnerClassLoader(urls);
                });
                if (!(classLoader instanceof URLClassLoader)) {
                    throw new RuntimeException("ClassLoader has to be an url class loader");
                }

                try (JarFile jarFile = new JarFile(path.toFile())) {
                    String main = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
                    Method invoke = classLoader.loadClass(main).getMethod("main", String[].class);

                    invokeMethod(invoke, args, classLoader);
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private static void startSetup(BiConsumer<String, String> andThen, String version) {
        unpackExecutor();

        if (Files.exists(Paths.get("reformcloud/.bin/config.properties")) && version == null) {
            andThen.accept(
                    PROPERTIES.getProperty("reformcloud.version"),
                    PROPERTIES.getProperty("reformcloud.type.id")
            );
            return;
        }

        if (PROPERTIES.containsKey("reformcloud.type.id")) {
            andThen.accept(write(PROPERTIES.getProperty("reformcloud.type.id"), version), PROPERTIES.getProperty("reformcloud.type.id"));
            return;
        }

        int type = getType();
        andThen.accept(write(Integer.toString(type), version), Integer.toString(type));
    }

    private static boolean isAPI() {
        return System.getProperty("reformcloud.executor.type") != null &&
                System.getProperty("reformcloud.executor.type").equals("3");
    }

    private static String write(String id, String newVersion) {
        String version = newVersion == null ? System.getProperty("reformcloud.runner.version") : newVersion;

        PROPERTIES.clear();
        PROPERTIES.setProperty("reformcloud.version", version);
        PROPERTIES.setProperty("reformcloud.type.id", id);

        try (OutputStream outputStream = Files.newOutputStream(Paths.get("reformcloud/.bin/config.properties"))) {
            PROPERTIES.store(outputStream, "ReformCloud runner configuration");
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return version;
    }

    private static int getType() {
        Integer type = getType0();
        if (type != null) {
            return type;
        }

        System.out.println(CHOOSE_INSTALL_MESSAGE.get());

        Console console = System.console();
        String s = console.readLine();
        while (s == null || s.trim().isEmpty() || !CONTROLLER_UNPACK_TEST.test(s)) {
            System.out.println(CHOOSE_INSTALL_MESSAGE.get());
            s = console.readLine();
        }

        return s.equalsIgnoreCase("controller") ? 1 : s.equalsIgnoreCase("node") ? 4 : 2;
    }

    private static void readProperties() {
        if (Files.exists(Paths.get("reformcloud/.bin/config.properties"))) {
            try (InputStream inputStream = Files.newInputStream(Paths.get("reformcloud/.bin/config.properties"))) {
                PROPERTIES.load(inputStream);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void setSystemProperties(String defaultValue) {
        System.setProperty(
                "reformcloud.runner.version",
                PROPERTIES.getProperty("reformcloud.version", defaultValue)
        );

        System.setProperty(
                "reformcloud.runner.specification",
                System.getProperty("reformcloud.runner.version").endsWith("-SNAPSHOT") ? "SNAPSHOT" : "RELEASE"
        );
    }

    private static void unpackExecutor() {
        if (Boolean.getBoolean("reformcloud.dev.mode")) {
            try {
                Files.deleteIfExists(Paths.get("reformcloud/.bin/executor.jar"));
            } catch (final IOException ex) {
                ex.printStackTrace();
            }

            System.out.println("DEBUG: Running in development mode on git:" + Runner.class.getPackage().getSpecificationVersion());
        }

        if (Files.exists(Paths.get("reformcloud/.bin/executor.jar"))) {
            return;
        }

        try (InputStream inputStream = Runner.class.getClassLoader().getResourceAsStream("files/executor.jar")) {
            if (inputStream == null) {
                throw new IllegalStateException("Could not find \"executor.jar\" in \"runner.jar\". This is not a bug of reformcloud.");
            }

            Files.createDirectories(Paths.get("reformcloud/.bin/libs"));
            Files.copy(inputStream, Paths.get("reformcloud/.bin/executor.jar"), StandardCopyOption.REPLACE_EXISTING);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createInvoke(String id) {
        System.setProperty("reformcloud.executor.type", id);
    }

    /* ================== */

    private static void runIfProcessExists(Consumer<Path> consumer) {
        String fileName = System.getProperty("reformcloud.process.path");
        if (fileName == null || !Files.exists(Paths.get(fileName))) {
            throw new RuntimeException("Cannot find process jar to execute");
        }

        consumer.accept(Paths.get(fileName));
    }

    private static void invokeMethod(Method method, Object args, ClassLoader classLoader) {
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            method.invoke(null, args);
        } catch (final InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    public static void premain(String string, Instrumentation inst) {
        if (System.getProperty("reformcloud.lib.path") == null || System.getProperty("reformcloud.process.path") == null) {
            return;
        }

        String libPath = System.getProperty("reformcloud.lib.path") + "/reformcloud/.bin/libs/";
        File file = new File(libPath);
        if (!file.exists() || !file.isDirectory()) {
            throw new RuntimeException("Bad lib path given " + libPath);
        }

        try {
            inst.appendToSystemClassLoaderSearch(new JarFile(new File(System.getProperty("reformcloud.process.path"))));
            for (File dependency : Objects.requireNonNull(file.listFiles())) {
                if (!dependency.getName().endsWith(".jar") || !dependency.isFile()) {
                    continue;
                }

                inst.appendToSystemClassLoaderSearch(new JarFile(file));
            }
        } catch (final Throwable ignored) {
        }
    }

    private static Integer getType0() {
        try {
            int type = Integer.parseInt(System.getProperty("runner.type"));
            if (type > 4 || type == 3 || type < 1) {
                throw new IllegalArgumentException("Illegal runner type given");
            }

            return type;
        } catch (final Throwable ignored) {
            return null;
        }
    }
}
