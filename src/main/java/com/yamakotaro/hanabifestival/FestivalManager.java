package com.yamakotaro.hanabifestival;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FestivalManager {

    private final HanabiFestivalPlugin plugin;
    private final PointManager pointManager;
    private final ShowManager showManager;
    private final MessageManager messages;

    private final List<BukkitTask> activeTasks = new ArrayList<>();
    private boolean running = false;
    private ShowDefinition currentShow;
    private long startedAtMillis;
    private long totalDurationSeconds;
    private BossBar bossBar;

    public FestivalManager(HanabiFestivalPlugin plugin, PointManager pointManager,
                            ShowManager showManager, MessageManager messages) {
        this.plugin = plugin;
        this.pointManager = pointManager;
        this.showManager = showManager;
        this.messages = messages;
    }

    public boolean isRunning() {
        return running;
    }

    public ShowDefinition getCurrentShow() {
        return currentShow;
    }

    public long getElapsedSeconds() {
        return running ? (System.currentTimeMillis() - startedAtMillis) / 1000L : 0L;
    }

    public boolean start(ShowDefinition show) {
        if (running) {
            return false;
        }
        running = true;
        currentShow = show;
        startedAtMillis = System.currentTimeMillis();
        totalDurationSeconds = 0L;

        int countdown = plugin.getConfig().getInt("countdown-seconds", 3);
        if (countdown > 0) {
            runCountdown(countdown, show);
        } else {
            beginShow(show);
        }
        return true;
    }

    public boolean stop() {
        if (!running) {
            return false;
        }
        running = false;
        for (BukkitTask task : activeTasks) {
            task.cancel();
        }
        activeTasks.clear();
        currentShow = null;

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
        plugin.getStageEffectManager().restore();
        plugin.getStatsManager().save();

        messages.broadcast("stop-broadcast", null);
        return true;
    }

    private void runCountdown(int seconds, ShowDefinition show) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("show", show.getDisplayNameColored());

        new BukkitRunnable() {
            int remaining = seconds;

            @Override
            public void run() {
                if (!running) {
                    cancel();
                    return;
                }
                if (remaining <= 0) {
                    beginShow(show);
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendTitle(String.valueOf(remaining), messages.format("countdown", placeholders), 0, 20, 0);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                }
                remaining--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void beginShow(ShowDefinition show) {
        if (!running) {
            return;
        }
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("show", show.getDisplayNameColored());
        messages.broadcast("start-broadcast", placeholders);

        applyStageEffects();

        if (show.getMode() == ShowMode.RANDOM) {
            startRandomMode(show);
        } else {
            startSequenceMode(show);
        }

        startAmbientParticles();
        startBossBar(show);

        long cheerInterval = plugin.getConfig().getLong("cheer-interval-seconds", 0L);
        if (cheerInterval > 0) {
            BukkitTask cheerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.6f, 1.2f);
                }
            }, cheerInterval * 20L, cheerInterval * 20L);
            activeTasks.add(cheerTask);
        }
    }

    private void startRandomMode(ShowDefinition show) {
        FireworkPreset preset = show.getPreset().mergeWithDefault(defaultPreset());
        int perLaunch = show.getFireworksPerLaunch();

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            int radius = preset.getRadius() != null ? preset.getRadius()
                    : plugin.getConfig().getInt("fallback-radius", 20);
            for (Location target : resolveTargets()) {
                for (int i = 0; i < perLaunch; i++) {
                    FireworkLauncher.launch(FireworkLauncher.randomLocationAround(target, radius), preset);
                    plugin.getStatsManager().recordLaunch();
                }
            }
        }, 0L, Math.max(1L, show.getIntervalTicks()));
        activeTasks.add(task);

        long duration = show.getDurationSeconds();
        totalDurationSeconds = duration;
        if (duration > 0) {
            BukkitTask endTask = Bukkit.getScheduler().runTaskLater(plugin, this::stop, duration * 20L);
            activeTasks.add(endTask);
        }
    }

    private void startSequenceMode(ShowDefinition show) {
        FireworkPreset showDefault = show.getPreset().mergeWithDefault(defaultPreset());

        long stepsDuration = 0L;
        for (ShowStep step : show.getSteps()) {
            stepsDuration = Math.max(stepsDuration, step.getDelayTicks());
        }

        int repeatCount = Math.max(1, show.getRepeat());
        for (int iteration = 0; iteration < repeatCount; iteration++) {
            long base = iteration * (stepsDuration + show.getRepeatDelayTicks());
            for (ShowStep step : show.getSteps()) {
                FireworkPreset preset = step.getPreset().mergeWithDefault(showDefault);
                int radius = preset.getRadius() != null ? preset.getRadius()
                        : plugin.getConfig().getInt("fallback-radius", 20);
                long delay = base + step.getDelayTicks();

                BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Location target : resolveTargets()) {
                        for (int i = 0; i < step.getCount(); i++) {
                            FireworkLauncher.launch(FireworkLauncher.randomLocationAround(target, radius), preset);
                            plugin.getStatsManager().recordLaunch();
                        }
                    }
                }, delay);
                activeTasks.add(task);
            }
        }

        long totalTicks = repeatCount * (stepsDuration + show.getRepeatDelayTicks());
        totalDurationSeconds = totalTicks / 20L;
        BukkitTask endTask = Bukkit.getScheduler().runTaskLater(plugin, this::stop, totalTicks + 20L);
        activeTasks.add(endTask);
    }

    private void startAmbientParticles() {
        if (!plugin.getConfig().getBoolean("ambient-particles", true) || pointManager.isEmpty()) {
            return;
        }
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (LaunchPoint point : pointManager.getAll().values()) {
                Location location = point.toLocation();
                if (location == null || location.getWorld() == null) {
                    continue;
                }
                location.getWorld().spawnParticle(Particle.FLAME, location.clone().add(0, 1, 0),
                        4, 0.4, 0.2, 0.4, 0.01);
            }
        }, 0L, 10L);
        activeTasks.add(task);
    }

    private void startBossBar(ShowDefinition show) {
        bossBar = Bukkit.createBossBar(bossBarTitle(show, 0), BarColor.YELLOW, BarStyle.SEGMENTED_10);
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long elapsed = getElapsedSeconds();
            for (Player player : Bukkit.getOnlinePlayers()) {
                bossBar.addPlayer(player);
            }
            bossBar.setTitle(bossBarTitle(show, elapsed));
            if (totalDurationSeconds > 0) {
                double progress = 1.0 - Math.min(1.0, (double) elapsed / totalDurationSeconds);
                bossBar.setProgress(Math.max(0.0, progress));
            } else {
                double wave = (Math.sin(elapsed * 0.5) + 1.0) / 2.0;
                bossBar.setProgress(Math.max(0.1, wave));
            }
        }, 0L, 20L);
        activeTasks.add(task);
    }

    private String bossBarTitle(ShowDefinition show, long elapsed) {
        Map<String, String> ph = new HashMap<>();
        ph.put("show", show.getDisplayNameColored());
        ph.put("elapsed", String.valueOf(elapsed));
        return messages.format("bossbar-title", ph);
    }

    private void applyStageEffects() {
        Set<World> worlds = new HashSet<>();
        for (Location location : resolveTargets()) {
            if (location.getWorld() != null) {
                worlds.add(location.getWorld());
            }
        }
        plugin.getStageEffectManager().apply(worlds);
    }

    private List<Location> resolveTargets() {
        List<Location> targets = new ArrayList<>();
        if (!pointManager.isEmpty()) {
            for (LaunchPoint point : pointManager.getAll().values()) {
                Location location = point.toLocation();
                if (location != null) {
                    targets.add(location);
                }
            }
        }
        if (targets.isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                targets.add(player.getLocation());
            }
        }
        return targets;
    }

    private FireworkPreset defaultPreset() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("default");
        int fallbackRadius = plugin.getConfig().getInt("fallback-radius", 20);
        if (section == null) {
            return new FireworkPreset(1, fallbackRadius, List.of("FFFFFF"), List.of("BALL"), true, true);
        }
        return new FireworkPreset(
                section.getInt("power", 1),
                fallbackRadius,
                section.getStringList("colors"),
                section.getStringList("types"),
                section.getBoolean("flicker", true),
                section.getBoolean("trail", true)
        );
    }
}
