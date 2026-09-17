package com.yamakotaro.hanabifestival;

import org.bukkit.plugin.java.JavaPlugin;

public final class HanabiFestivalPlugin extends JavaPlugin {

    private FestivalManager festivalManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.festivalManager = new FestivalManager(this);
        HanabiCommand command = new HanabiCommand(this, festivalManager);
        getCommand("hanabi").setExecutor(command);
        getCommand("hanabi").setTabCompleter(command);
    }

    @Override
    public void onDisable() {
        if (festivalManager != null) {
            festivalManager.stop(false);
        }
    }

    public FestivalManager getFestivalManager() {
        return festivalManager;
    }
}
