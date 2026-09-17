package com.yamakotaro.hanabifestival.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public class HanabiGuiHolder implements InventoryHolder {

    private Inventory inventory;
    private final Map<Integer, Runnable> actions = new HashMap<>();

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setAction(int slot, Runnable action) {
        actions.put(slot, action);
    }

    public Runnable getAction(int slot) {
        return actions.get(slot);
    }
}
