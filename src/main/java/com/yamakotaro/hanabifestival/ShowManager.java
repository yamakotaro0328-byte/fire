package com.yamakotaro.hanabifestival;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShowManager {

    private final HanabiFestivalPlugin plugin;
    private final File file;
    private final Map<String, ShowDefinition> shows = new LinkedHashMap<>();

    public ShowManager(HanabiFestivalPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "shows.yml");
        if (!file.exists()) {
            plugin.saveResource("shows.yml", false);
        }
        load();
    }

    public void load() {
        shows.clear();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = config.getConfigurationSection("shows");
        if (root == null) {
            return;
        }
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            shows.put(id.toLowerCase(), parseShow(id, section));
        }
    }

    private ShowDefinition parseShow(String id, ConfigurationSection section) {
        String displayName = section.getString("display-name", id);
        ShowMode mode = ShowMode.parse(section.getString("mode", "random"));
        FireworkPreset preset = readPreset(section);

        if (mode == ShowMode.SEQUENCE) {
            List<ShowStep> steps = new ArrayList<>();
            for (Map<?, ?> raw : section.getMapList("steps")) {
                steps.add(parseStep(raw));
            }
            int repeat = section.getInt("repeat", 1);
            long repeatDelay = section.getLong("repeat-delay-ticks", 0L);
            return new ShowDefinition(id, displayName, mode, 0L, 0, 0L, steps, repeat, repeatDelay, preset);
        }

        long interval = section.getLong("interval-ticks", 20L);
        int perLaunch = section.getInt("fireworks-per-launch", 3);
        long duration = section.getLong("duration-seconds", 0L);
        return new ShowDefinition(id, displayName, mode, interval, perLaunch, duration, List.of(), 1, 0L, preset);
    }

    @SuppressWarnings("unchecked")
    private ShowStep parseStep(Map<?, ?> raw) {
        long delay = asLong(raw.get("delay-ticks"), 0L);
        int count = (int) asLong(raw.get("count"), 1L);
        Integer power = raw.containsKey("power") ? (int) asLong(raw.get("power"), 1L) : null;
        Integer radius = raw.containsKey("radius") ? (int) asLong(raw.get("radius"), 0L) : null;
        List<String> colors = raw.get("colors") instanceof List ? (List<String>) raw.get("colors") : null;
        List<String> types = raw.get("types") instanceof List ? (List<String>) raw.get("types") : null;
        Boolean flicker = raw.get("flicker") instanceof Boolean ? (Boolean) raw.get("flicker") : null;
        Boolean trail = raw.get("trail") instanceof Boolean ? (Boolean) raw.get("trail") : null;
        FireworkPreset preset = new FireworkPreset(power, radius, colors, types, flicker, trail);
        return new ShowStep(delay, count, preset);
    }

    private long asLong(Object value, long fallback) {
        return value instanceof Number ? ((Number) value).longValue() : fallback;
    }

    private FireworkPreset readPreset(ConfigurationSection section) {
        Integer power = section.contains("power") ? section.getInt("power") : null;
        Integer radius = section.contains("radius") ? section.getInt("radius") : null;
        List<String> colors = section.contains("colors") ? section.getStringList("colors") : null;
        List<String> types = section.contains("types") ? section.getStringList("types") : null;
        Boolean flicker = section.contains("flicker") ? section.getBoolean("flicker") : null;
        Boolean trail = section.contains("trail") ? section.getBoolean("trail") : null;
        return new FireworkPreset(power, radius, colors, types, flicker, trail);
    }

    public ShowDefinition get(String id) {
        return shows.get(id.toLowerCase());
    }

    public boolean exists(String id) {
        return shows.containsKey(id.toLowerCase());
    }

    public List<String> getIds() {
        return new ArrayList<>(shows.keySet());
    }
}
