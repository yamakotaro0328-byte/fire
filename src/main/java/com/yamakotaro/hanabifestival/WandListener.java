package com.yamakotaro.hanabifestival;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WandListener implements Listener {

    private static final String WAND_NAME = ChatColor.GOLD + "花火エリア選択ツール";

    private final HanabiFestivalPlugin plugin;

    public WandListener(HanabiFestivalPlugin plugin) {
        this.plugin = plugin;
    }

    public static ItemStack createWand() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(WAND_NAME);
        meta.setLore(List.of(
                ChatColor.GRAY + "左クリック: 1点目を設定",
                ChatColor.GRAY + "右クリック: 2点目を設定"));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.BLAZE_ROD || !item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !WAND_NAME.equals(meta.getDisplayName())) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        Player player = event.getPlayer();
        event.setCancelled(true);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            plugin.getSelectionManager().setPos1(player.getUniqueId(), event.getClickedBlock().getLocation());
            plugin.getMessageManager().send(player, "wand-pos1");
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            plugin.getSelectionManager().setPos2(player.getUniqueId(), event.getClickedBlock().getLocation());
            plugin.getMessageManager().send(player, "wand-pos2");
        }
    }
}
