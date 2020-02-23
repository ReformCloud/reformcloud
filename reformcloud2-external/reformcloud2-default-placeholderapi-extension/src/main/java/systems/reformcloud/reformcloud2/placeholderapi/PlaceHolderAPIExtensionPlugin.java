package systems.reformcloud.reformcloud2.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import systems.reformcloud.reformcloud2.placeholderapi.api.ReformCloudPlaceHolderExpansion;

public class PlaceHolderAPIExtensionPlugin extends JavaPlugin {

    private final PlaceholderExpansion placeholderExpansion = new ReformCloudPlaceHolderExpansion();

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return;
        }

        placeholderExpansion.register();
    }

    @Override
    public void onDisable() {
        PlaceholderAPI.unregisterExpansion(placeholderExpansion);
    }
}
