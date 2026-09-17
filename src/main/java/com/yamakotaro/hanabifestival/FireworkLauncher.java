package com.yamakotaro.hanabifestival;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class FireworkLauncher {

    private static final Random RANDOM = new Random();

    private FireworkLauncher() {
    }

    public static Location randomLocationAround(Location center, int radius) {
        World world = center.getWorld();
        if (world == null || radius <= 0) {
            return center.clone();
        }
        double angle = RANDOM.nextDouble() * Math.PI * 2;
        double distance = RANDOM.nextDouble() * radius;
        double x = center.getX() + Math.cos(angle) * distance;
        double z = center.getZ() + Math.sin(angle) * distance;
        int y = world.getHighestBlockYAt((int) Math.floor(x), (int) Math.floor(z)) + 1;
        return new Location(world, x, y, z);
    }

    public static void launch(Location location, FireworkPreset preset) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        Firework firework = world.spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        List<Color> colors = toColors(preset.getColors());
        List<Color> fadeColors = toColors(preset.getColors());
        List<FireworkEffect.Type> types = toTypes(preset.getTypes());

        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.withColor(pick(colors, Color.WHITE));
        if (!fadeColors.isEmpty() && RANDOM.nextBoolean()) {
            builder.withFade(pick(fadeColors, Color.WHITE));
        }
        builder.with(pick(types, FireworkEffect.Type.BALL));

        boolean flicker = preset.getFlicker() == null || preset.getFlicker();
        boolean trail = preset.getTrail() == null || preset.getTrail();
        builder.flicker(flicker && RANDOM.nextBoolean());
        builder.trail(trail && RANDOM.nextBoolean());

        meta.addEffect(builder.build());
        int power = preset.getPower() != null ? preset.getPower() : 1;
        meta.setPower(Math.max(0, Math.min(3, power)));
        firework.setFireworkMeta(meta);
    }

    private static List<Color> toColors(List<String> hexColors) {
        List<Color> colors = new ArrayList<>();
        if (hexColors == null) {
            return colors;
        }
        for (String hex : hexColors) {
            try {
                colors.add(Color.fromRGB(Integer.parseInt(hex.trim(), 16)));
            } catch (NumberFormatException ignored) {
                // 不正な色設定は無視する
            }
        }
        return colors;
    }

    private static List<FireworkEffect.Type> toTypes(List<String> names) {
        List<FireworkEffect.Type> types = new ArrayList<>();
        if (names == null) {
            return types;
        }
        for (String name : names) {
            try {
                types.add(FireworkEffect.Type.valueOf(name.trim().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                // 不正な形状設定は無視する
            }
        }
        return types;
    }

    private static <T> T pick(List<T> list, T fallback) {
        if (list.isEmpty()) {
            return fallback;
        }
        return list.get(RANDOM.nextInt(list.size()));
    }
}
