package com.yamakotaro.hanabifestival;

import org.bukkit.ChatColor;

import java.util.List;

public class ShowDefinition {

    private final String id;
    private final String displayName;
    private final ShowMode mode;

    private final long intervalTicks;
    private final int fireworksPerLaunch;
    private final long durationSeconds;

    private final List<ShowStep> steps;
    private final int repeat;
    private final long repeatDelayTicks;

    private final FireworkPreset preset;

    public ShowDefinition(String id, String displayName, ShowMode mode,
                           long intervalTicks, int fireworksPerLaunch, long durationSeconds,
                           List<ShowStep> steps, int repeat, long repeatDelayTicks,
                           FireworkPreset preset) {
        this.id = id;
        this.displayName = displayName;
        this.mode = mode;
        this.intervalTicks = intervalTicks;
        this.fireworksPerLaunch = fireworksPerLaunch;
        this.durationSeconds = durationSeconds;
        this.steps = steps;
        this.repeat = repeat;
        this.repeatDelayTicks = repeatDelayTicks;
        this.preset = preset;
    }

    public String getId() {
        return id;
    }

    public String getDisplayNameColored() {
        return ChatColor.translateAlternateColorCodes('&', displayName);
    }

    public ShowMode getMode() {
        return mode;
    }

    public long getIntervalTicks() {
        return intervalTicks;
    }

    public int getFireworksPerLaunch() {
        return fireworksPerLaunch;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public List<ShowStep> getSteps() {
        return steps;
    }

    public int getRepeat() {
        return repeat;
    }

    public long getRepeatDelayTicks() {
        return repeatDelayTicks;
    }

    public FireworkPreset getPreset() {
        return preset;
    }
}
