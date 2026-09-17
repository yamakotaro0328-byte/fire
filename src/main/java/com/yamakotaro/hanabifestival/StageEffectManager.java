package com.yamakotaro.hanabifestival;

import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

/** 花火大会の開催中、演出のために天候・時間を制御し、終了後に元の状態へ復元する。 */
public class StageEffectManager {

    private final HanabiFestivalPlugin plugin;
    private final Map<String, SavedState> savedStates = new HashMap<>();

    public StageEffectManager(HanabiFestivalPlugin plugin) {
        this.plugin = plugin;
    }

    public void apply(Iterable<World> worlds) {
        boolean forceNight = plugin.getConfig().getBoolean("stage-effects.force-night", false);
        boolean forceClear = plugin.getConfig().getBoolean("stage-effects.force-clear-weather", false);
        if (!forceNight && !forceClear) {
            return;
        }
        for (World world : worlds) {
            savedStates.putIfAbsent(world.getName(),
                    new SavedState(world.getTime(), world.hasStorm(), world.isThundering()));
            if (forceNight) {
                world.setTime(18000L);
            }
            if (forceClear) {
                world.setStorm(false);
                world.setThundering(false);
            }
        }
    }

    public void restore() {
        for (Map.Entry<String, SavedState> entry : savedStates.entrySet()) {
            World world = plugin.getServer().getWorld(entry.getKey());
            if (world == null) {
                continue;
            }
            SavedState state = entry.getValue();
            world.setTime(state.time);
            world.setStorm(state.storm);
            world.setThundering(state.thundering);
        }
        savedStates.clear();
    }

    private static final class SavedState {
        private final long time;
        private final boolean storm;
        private final boolean thundering;

        private SavedState(long time, boolean storm, boolean thundering) {
            this.time = time;
            this.storm = storm;
            this.thundering = thundering;
        }
    }
}
