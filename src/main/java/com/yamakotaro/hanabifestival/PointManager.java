package com.yamakotaro.hanabifestival;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PointManager {

    private final HanabiFestivalPlugin plugin;
    private final File file;
    private final Map<String, LaunchPoint> points = new LinkedHashMap<>();

    public PointManager(HanabiFestivalPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "points.yml");
        load();
    }

    public void load() {
        points.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if (!yaml.isConfigurationSection("points")) {
            return;
        }
        for (String name : yaml.getConfigurationSection("points").getKeys(false)) {
            String path = "points." + name + ".";
            String world = yaml.getString(path + "world");
            if (world == null) {
                continue;
            }
            double x = yaml.getDouble(path + "x");
            double y = yaml.getDouble(path + "y");
            double z = yaml.getDouble(path + "z");
            points.put(name, new LaunchPoint(name, world, x, y, z));
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (LaunchPoint point : points.values()) {
            String path = "points." + point.getName() + ".";
            yaml.set(path + "world", point.getWorldName());
            yaml.set(path + "x", point.getX());
            yaml.set(path + "y", point.getY());
            yaml.set(path + "z", point.getZ());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("points.yml の保存に失敗しました: " + e.getMessage());
        }
    }

    public void set(String name, Location location) {
        World world = location.getWorld();
        points.put(name, new LaunchPoint(name, world != null ? world.getName() : "world",
                location.getX(), location.getY(), location.getZ()));
        save();
    }

    public boolean remove(String name) {
        boolean removed = points.remove(name) != null;
        if (removed) {
            save();
        }
        return removed;
    }

    public Set<String> getNames() {
        return points.keySet();
    }

    public Map<String, LaunchPoint> getAll() {
        return points;
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }
}
