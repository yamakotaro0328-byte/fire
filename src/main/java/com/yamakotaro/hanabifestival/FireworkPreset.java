package com.yamakotaro.hanabifestival;

import java.util.List;

public class FireworkPreset {

    private final Integer power;
    private final Integer radius;
    private final List<String> colors;
    private final List<String> types;
    private final Boolean flicker;
    private final Boolean trail;

    public FireworkPreset(Integer power, Integer radius, List<String> colors,
                           List<String> types, Boolean flicker, Boolean trail) {
        this.power = power;
        this.radius = radius;
        this.colors = colors;
        this.types = types;
        this.flicker = flicker;
        this.trail = trail;
    }

    public Integer getPower() {
        return power;
    }

    public Integer getRadius() {
        return radius;
    }

    public List<String> getColors() {
        return colors;
    }

    public List<String> getTypes() {
        return types;
    }

    public Boolean getFlicker() {
        return flicker;
    }

    public Boolean getTrail() {
        return trail;
    }

    /** このプリセットで未指定の項目を fallback の値で補完した新しいプリセットを返す。 */
    public FireworkPreset mergeWithDefault(FireworkPreset fallback) {
        return new FireworkPreset(
                power != null ? power : fallback.power,
                radius != null ? radius : fallback.radius,
                (colors != null && !colors.isEmpty()) ? colors : fallback.colors,
                (types != null && !types.isEmpty()) ? types : fallback.types,
                flicker != null ? flicker : fallback.flicker,
                trail != null ? trail : fallback.trail
        );
    }
}
