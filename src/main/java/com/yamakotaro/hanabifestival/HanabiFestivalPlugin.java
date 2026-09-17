package com.yamakotaro.hanabifestival;

import com.yamakotaro.hanabifestival.gui.GuiListener;
import com.yamakotaro.hanabifestival.gui.GuiManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HanabiFestivalPlugin extends JavaPlugin {

    private MessageManager messageManager;
    private AreaManager areaManager;
    private PointManager pointManager;
    private SelectionManager selectionManager;
    private ShowManager showManager;
    private StatsManager statsManager;
    private StageEffectManager stageEffectManager;
    private FestivalManager festivalManager;
    private GuiManager guiManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.messageManager = new MessageManager(this);
        this.areaManager = new AreaManager(this);
        this.pointManager = new PointManager(this);
        this.selectionManager = new SelectionManager();
        this.showManager = new ShowManager(this);
        this.statsManager = new StatsManager(this);
        this.stageEffectManager = new StageEffectManager(this);
        this.festivalManager = new FestivalManager(this, areaManager, pointManager, showManager, messageManager);
        this.guiManager = new GuiManager(this, festivalManager);

        HanabiCommand command = new HanabiCommand(this);
        getCommand("hanabi").setExecutor(command);
        getCommand("hanabi").setTabCompleter(command);

        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(new WandListener(this), this);
    }

    @Override
    public void onDisable() {
        if (festivalManager != null) {
            festivalManager.stop();
        }
        if (statsManager != null) {
            statsManager.save();
        }
    }

    public void reloadAll() {
        reloadConfig();
        messageManager.load();
        areaManager.load();
        pointManager.load();
        showManager.load();
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public AreaManager getAreaManager() {
        return areaManager;
    }

    public PointManager getPointManager() {
        return pointManager;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public ShowManager getShowManager() {
        return showManager;
    }

    public FestivalManager getFestivalManager() {
        return festivalManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    public StageEffectManager getStageEffectManager() {
        return stageEffectManager;
    }
}
