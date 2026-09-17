package com.yamakotaro.hanabifestival;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LaunchPoint {

    private final String name;
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;

    public LaunchPoint(String name, String worldName, double x, double y, double z) {
        this.name = name;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Location toLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z);
    }
}
