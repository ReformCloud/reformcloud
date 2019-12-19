package systems.reformcloud.reformcloud2.executor.api.bungee.plugins;

import com.google.common.collect.Multimap;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.*;
import org.yaml.snakeyaml.Yaml;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.executor.PluginExecutor;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Handler;

public final class PluginExecutorContainer implements PluginExecutor {

    static {
        try {
            Method method = Plugin.class.getDeclaredMethod("init", ProxyServer.class, PluginDescription.class);
            method.setAccessible(true);
            init = method;

            Field field = PluginManager.class.getDeclaredField("plugins");
            field.setAccessible(true);
            plugins = field;

            Field field1 = PluginManager.class.getDeclaredField("commandsByPlugin");
            field1.setAccessible(true);
            commands = field1;

            Field field2 = PluginManager.class.getDeclaredField("listenersByPlugin");
            field2.setAccessible(true);
            listeners = field2;

            Field field3 = PluginClassloader.class.getDeclaredField("allLoaders");
            field3.setAccessible(true);
            allLoaders = field3;
        } catch (final NoSuchMethodException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    private static Method init;

    private static Field plugins;

    private static Field commands;

    private static Field listeners;

    private static Field allLoaders;

    @Override
    public void installPlugin(InstallablePlugin installablePlugin) {
        if (Files.exists(Paths.get("plugins/" + installablePlugin.getName()))
                || ProxyServer.getInstance().getPluginManager().getPlugin(installablePlugin.getName()) != null) {
            return;
        }

        DownloadHelper.downloadAndDisconnect(installablePlugin.getDownloadURL(), "plugins/" + installablePlugin.getName() + ".jar");
        loadPlugin(installablePlugin);
    }

    @Override
    public void uninstallPlugin(systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin plugin) {
        disablePlugin(ProxyServer.getInstance().getPluginManager().getPlugin(plugin.getName()));
    }

    private void loadPlugin(InstallablePlugin plugin) {
        final File pluginFile = new File("plugins", plugin.getName() + ".jar");

        try (JarFile jarFile = new JarFile(pluginFile)) {
            JarEntry descriptionFile = jarFile.getJarEntry("bungee.yml");
            if (descriptionFile == null) {
                descriptionFile = jarFile.getJarEntry("plugin.yml");
            }
            Conditions.isTrue(descriptionFile != null, "Plugin does not contain plugin or bungee description file");
            try (InputStream inputStream = jarFile.getInputStream(descriptionFile)) {
                PluginDescription description = new Yaml().loadAs(inputStream, PluginDescription.class);
                description.setFile(pluginFile);

                //depends
                Collection<String> loaded = Links.apply(ProxyServer.getInstance().getPluginManager().getPlugins(), plugin1 -> plugin1.getDescription().getName());
                for (String depend : description.getDepends()) {
                    Conditions.isTrue(loaded.contains(depend), depend + " required by " + description.getName() + " is not loaded");
                }

                URLClassLoader classLoader = new PluginClassloader(new URL[]{pluginFile.toURI().toURL()});
                Class<?> mainClass = classLoader.loadClass(description.getMain());
                Plugin actual = (Plugin) mainClass.getDeclaredConstructor().newInstance();

                //Init plugin
                init.invoke(actual, ProxyServer.getInstance(), description);

                //Add plugin
                Map<String, Plugin> map = (Map<String, Plugin>) plugins.get(ProxyServer.getInstance().getPluginManager());
                map.put(description.getName(), actual);

                actual.onLoad();
                actual.onEnable();
            }
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void disablePlugin(Plugin plugin) {
        if (plugin == null) {
            return;
        }

        final PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
        final ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();

        try {
            plugin.onDisable();
            for (Handler handler : plugin.getLogger().getHandlers()) {
                handler.close();
            }
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }

        pluginManager.unregisterListeners(plugin);
        pluginManager.unregisterCommands(plugin);
        ProxyServer.getInstance().getScheduler().cancel(plugin);
        plugin.getExecutorService().shutdownNow(); // This field is actually deprecated may be removed in further releases (Take care of it)

        Thread.getAllStackTraces().keySet().forEach(thread -> {
            if (thread.getContextClassLoader().equals(pluginClassLoader)) {
                thread.interrupt();
            }
        });

        try {
            Map<String, Plugin> pluginMap = (Map<String, Plugin>) plugins.get(pluginManager);
            pluginMap.values().remove(plugin);

            Multimap<Plugin, Command> commandMultimap = (Multimap<Plugin, Command>) commands.get(pluginManager);
            commandMultimap.removeAll(plugin);

            Multimap<Plugin, Listener> listenerMultimap = (Multimap<Plugin, Listener>) listeners.get(pluginManager);
            listenerMultimap.removeAll(plugin);
        } catch (final IllegalAccessException ex) {
            ex.printStackTrace();
        }

        if (pluginClassLoader instanceof URLClassLoader) {
            try {
                ((URLClassLoader) pluginClassLoader).close();
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        try {
            Set<PluginClassloader> pluginClassLoaders = (Set<PluginClassloader>) allLoaders.get(null);
            assert pluginClassLoader instanceof PluginClassloader;
            pluginClassLoaders.remove(pluginClassLoader);
        } catch (final IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}
