package com.yamakotaro.hanabifestival;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AreaManager {

    private final HanabiFestivalPlugin plugin;
    private final File file;
    private final Map<String, Region> areas = new LinkedHashMap<>();

    public AreaManager(HanabiFestivalPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "areas.yml");
        load();
    }

    public void load() {
        areas.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if (!yaml.isConfigurationSection("areas")) {
            return;
        }
        for (String name : yaml.getConfigurationSection("areas").getKeys(false)) {
            String path = "areas." + name + ".";
            String world = yaml.getString(path + "world");
            if (world == null) {
                continue;
            }
            areas.put(name, new Region(name, world,
                    yaml.getDouble(path + "x1"), yaml.getDouble(path + "y1"), yaml.getDouble(path + "z1"),
                    yaml.getDouble(path + "x2"), yaml.getDouble(path + "y2"), yaml.getDouble(path + "z2")));
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Region region : areas.values()) {
            String path = "areas." + region.getName() + ".";
            yaml.set(path + "world", region.getWorldName());
            yaml.set(path + "x1", region.getMinX());
            yaml.set(path + "y1", region.getMinY());
            yaml.set(path + "z1", region.getMinZ());
            yaml.set(path + "x2", region.getMaxX());
            yaml.set(path + "y2", region.getMaxY());
            yaml.set(path + "z2", region.getMaxZ());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("areas.yml の保存に失敗しました: " + e.getMessage());
        }
    }

    public void set(String name, Location pos1, Location pos2) {
        areas.put(name, new Region(name, pos1.getWorld().getName(),
                pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ()));
        save();
    }

    public boolean remove(String name) {
        boolean removed = areas.remove(name) != null;
        if (removed) {
            save();
        }
        return removed;
    }

    public Set<String> getNames() {
        return areas.keySet();
    }

    public Map<String, Region> getAll() {
        return areas;
    }

    public boolean isEmpty() {
        return areas.isEmpty();
    }
}
