package com.yamakotaro.hanabifestival;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

/** 2点で定義される直方体の打ち上げエリア。 */
public class Region {

    private final String name;
    private final String worldName;
    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public Region(String name, String worldName, double x1, double y1, double z1,
                  double x2, double y2, double z2) {
        this.name = name;
        this.worldName = worldName;
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    /**
     * エリア内のランダムな座標を返す。
     * Y座標に幅がある場合はその範囲内からランダムに選び、
     * 幅がない（同じ高さで選択された）場合は地表の高さを自動で使用する。
     */
    public Location randomLocation(Random random) {
        World world = getWorld();
        if (world == null) {
            return null;
        }
        double x = minX + random.nextDouble() * Math.max(0.0001, maxX - minX);
        double z = minZ + random.nextDouble() * Math.max(0.0001, maxZ - minZ);
        double y;
        if (maxY - minY > 0.5) {
            y = minY + random.nextDouble() * (maxY - minY);
        } else {
            y = world.getHighestBlockYAt((int) Math.floor(x), (int) Math.floor(z)) + 1;
        }
        return new Location(world, x, y, z);
    }
}
