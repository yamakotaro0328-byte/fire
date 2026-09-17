package com.yamakotaro.hanabifestival.gui;

import com.yamakotaro.hanabifestival.FestivalManager;
import com.yamakotaro.hanabifestival.HanabiFestivalPlugin;
import com.yamakotaro.hanabifestival.ShowDefinition;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GuiManager {

    private final HanabiFestivalPlugin plugin;
    private final FestivalManager festivalManager;

    public GuiManager(HanabiFestivalPlugin plugin, FestivalManager festivalManager) {
        this.plugin = plugin;
        this.festivalManager = festivalManager;
    }

    public void open(Player player) {
        HanabiGuiHolder holder = new HanabiGuiHolder();
        Inventory inventory = plugin.getServer().createInventory(holder, 27,
                ChatColor.translateAlternateColorCodes('&', "&6花火大会 コントロールパネル"));
        holder.setInventory(inventory);

        int slot = 10;
        for (String id : plugin.getShowManager().getIds()) {
            if (slot > 16) {
                break;
            }
            ShowDefinition show = plugin.getShowManager().get(id);
            ItemStack item = named(Material.FIREWORK_ROCKET, show.getDisplayNameColored(), "クリックしてこのショーを開始");
            inventory.setItem(slot, item);
            holder.setAction(slot, () -> {
                if (!festivalManager.isRunning()) {
                    festivalManager.start(show);
                }
                player.closeInventory();
            });
            slot++;
        }

        ItemStack stopItem = named(Material.BARRIER, ChatColor.RED + "花火大会を終了", "クリックして終了");
        inventory.setItem(22, stopItem);
        holder.setAction(22, () -> {
            festivalManager.stop();
            player.closeInventory();
        });

        ItemStack reloadItem = named(Material.BOOK, ChatColor.YELLOW + "設定を再読み込み", "クリックしてリロード");
        inventory.setItem(26, reloadItem);
        holder.setAction(26, () -> {
            plugin.reloadAll();
            player.closeInventory();
        });

        player.openInventory(inventory);
    }

    private ItemStack named(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(ChatColor.GRAY + lore));
        item.setItemMeta(meta);
        return item;
    }
}
