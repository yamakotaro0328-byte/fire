package com.yamakotaro.hanabifestival;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** 花火大会の累計打ち上げ数と、プレイヤーごとの観覧数を記録・永続化する。 */
public class StatsManager {

    private final HanabiFestivalPlugin plugin;
    private final File file;
    private long totalLaunched;
    private final Map<UUID, PlayerStat> playerStats = new LinkedHashMap<>();

    public StatsManager(HanabiFestivalPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "stats.yml");
        load();
    }

    public void load() {
        totalLaunched = 0L;
        playerStats.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        totalLaunched = yaml.getLong("total-launched", 0L);
        if (yaml.isConfigurationSection("players")) {
            for (String key : yaml.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String name = yaml.getString("players." + key + ".name", key);
                    long watched = yaml.getLong("players." + key + ".watched", 0L);
                    playerStats.put(uuid, new PlayerStat(name, watched));
                } catch (IllegalArgumentException ignored) {
                    // 不正なUUIDは無視する
                }
            }
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("total-launched", totalLaunched);
        for (Map.Entry<UUID, PlayerStat> entry : playerStats.entrySet()) {
            String path = "players." + entry.getKey() + ".";
            yaml.set(path + "name", entry.getValue().name);
            yaml.set(path + "watched", entry.getValue().watched);
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("stats.yml の保存に失敗しました: " + e.getMessage());
        }
    }

    /** 花火が1発打ち上げられるたびに呼び出し、累計数とオンラインプレイヤーの観覧数を加算する。 */
    public void recordLaunch() {
        totalLaunched++;
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerStat stat = playerStats.computeIfAbsent(player.getUniqueId(),
                    id -> new PlayerStat(player.getName(), 0L));
            stat.name = player.getName();
            stat.watched++;
        }
    }

    public long getTotalLaunched() {
        return totalLaunched;
    }

    public List<PlayerStat> getTopViewers(int limit) {
        return playerStats.values().stream()
                .sorted((a, b) -> Long.compare(b.watched, a.watched))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public static final class PlayerStat {
        private String name;
        private long watched;

        public PlayerStat(String name, long watched) {
            this.name = name;
            this.watched = watched;
        }

        public String getName() {
            return name;
        }

        public long getWatched() {
            return watched;
        }
    }
}
