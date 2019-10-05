package systems.reformcloud.reformcloud2.executor.api.common.plugins;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;

public abstract class Plugin implements Nameable {

    public static final TypeToken<DefaultPlugin> TYPE = new TypeToken<DefaultPlugin>() {};

    public abstract String version();

    public abstract String author();

    public abstract String main();

    public abstract List<String> depends();

    public abstract List<String> softpends();

    public abstract boolean enabled();
}
