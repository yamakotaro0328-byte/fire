package com.yamakotaro.hanabifestival;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public class MessageManager {

    private final HanabiFestivalPlugin plugin;
    private final File file;
    private YamlConfiguration config;

    public MessageManager(HanabiFestivalPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        load();
    }

    public void load() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public String format(String key) {
        return format(key, null);
    }

    public String format(String key, Map<String, String> placeholders) {
        String prefix = config.getString("prefix", "");
        String message = config.getString(key, key);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    public void send(CommandSender sender, String key) {
        sender.sendMessage(format(key));
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        sender.sendMessage(format(key, placeholders));
    }

    public void broadcast(String key, Map<String, String> placeholders) {
        plugin.getServer().broadcastMessage(format(key, placeholders));
    }
}
