package com.yamakotaro.hanabifestival.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof HanabiGuiHolder holder)) {
            return;
        }
        event.setCancelled(true);
        Runnable action = holder.getAction(event.getRawSlot());
        if (action != null) {
            action.run();
        }
    }
}
