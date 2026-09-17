package com.yamakotaro.hanabifestival;

public class ShowStep {

    private final long delayTicks;
    private final int count;
    private final FireworkPreset preset;

    public ShowStep(long delayTicks, int count, FireworkPreset preset) {
        this.delayTicks = delayTicks;
        this.count = count;
        this.preset = preset;
    }

    public long getDelayTicks() {
        return delayTicks;
    }

    public int getCount() {
        return count;
    }

    public FireworkPreset getPreset() {
        return preset;
    }
}
