package com.yamakotaro.hanabifestival;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FestivalManager {

    private final HanabiFestivalPlugin plugin;
    private final Random random = new Random();

    private BukkitTask launchTask;
    private BukkitTask endTask;
    private boolean running = false;

    public FestivalManager(HanabiFestivalPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean start() {
        if (running) {
            return false;
        }
        running = true;

        long interval = Math.max(1L, plugin.getConfig().getLong("interval-ticks", 20L));
        launchTask = Bukkit.getScheduler().runTaskTimer(plugin, this::launchWave, 0L, interval);

        long durationSeconds = plugin.getConfig().getLong("duration-seconds", 0L);
        if (durationSeconds > 0) {
            endTask = Bukkit.getScheduler().runTaskLater(plugin, () -> stop(true), durationSeconds * 20L);
        }
        return true;
    }

    public boolean stop(boolean announce) {
        if (!running) {
            return false;
        }
        running = false;
        if (launchTask != null) {
            launchTask.cancel();
            launchTask = null;
        }
        if (endTask != null) {
            endTask.cancel();
            endTask = null;
        }
        return true;
    }

    private void launchWave() {
        int perLaunch = Math.max(1, plugin.getConfig().getInt("fireworks-per-launch", 3));
        int radius = Math.max(0, plugin.getConfig().getInt("radius", 20));

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < perLaunch; i++) {
                Location location = randomLocationAround(player.getLocation(), radius);
                spawnFirework(location);
            }
        }
    }

    private Location randomLocationAround(Location center, int radius) {
        World world = center.getWorld();
        if (radius <= 0 || world == null) {
            return center.clone();
        }
        double angle = random.nextDouble() * Math.PI * 2;
        double distance = random.nextDouble() * radius;
        double x = center.getX() + Math.cos(angle) * distance;
        double z = center.getZ() + Math.sin(angle) * distance;
        int y = world.getHighestBlockYAt((int) Math.floor(x), (int) Math.floor(z)) + 1;
        return new Location(world, x, y, z);
    }

    private void spawnFirework(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        Firework firework = world.spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        List<Color> colors = loadColors();
        List<Color> fadeColors = loadColors();
        List<FireworkEffect.Type> types = loadTypes();

        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.withColor(pickRandom(colors, Color.WHITE));
        if (!fadeColors.isEmpty() && random.nextBoolean()) {
            builder.withFade(pickRandom(fadeColors, Color.WHITE));
        }
        builder.with(pickRandom(types, FireworkEffect.Type.BALL));
        builder.flicker(plugin.getConfig().getBoolean("flicker", true) && random.nextBoolean());
        builder.trail(plugin.getConfig().getBoolean("trail", true) && random.nextBoolean());

        meta.addEffect(builder.build());
        meta.setPower(Math.max(0, Math.min(3, plugin.getConfig().getInt("power", 1))));
        firework.setFireworkMeta(meta);
    }

    private List<Color> loadColors() {
        List<String> hexColors = plugin.getConfig().getStringList("colors");
        List<Color> colors = new ArrayList<>();
        for (String hex : hexColors) {
            try {
                colors.add(Color.fromRGB(Integer.parseInt(hex.trim(), 16)));
            } catch (NumberFormatException ignored) {
                // 不正な色設定は無視する
            }
        }
        return colors;
    }

    private List<FireworkEffect.Type> loadTypes() {
        List<String> names = plugin.getConfig().getStringList("types");
        List<FireworkEffect.Type> types = new ArrayList<>();
        for (String name : names) {
            try {
                types.add(FireworkEffect.Type.valueOf(name.trim().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                // 不正な形状設定は無視する
            }
        }
        return types;
    }

    private <T> T pickRandom(List<T> list, T fallback) {
        if (list.isEmpty()) {
            return fallback;
        }
        return list.get(random.nextInt(list.size()));
    }
}
