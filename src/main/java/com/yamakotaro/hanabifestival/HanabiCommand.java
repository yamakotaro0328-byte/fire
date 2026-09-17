package com.yamakotaro.hanabifestival;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HanabiCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("start", "stop", "reload");

    private final HanabiFestivalPlugin plugin;
    private final FestivalManager festivalManager;

    public HanabiCommand(HanabiFestivalPlugin plugin, FestivalManager festivalManager) {
        this.plugin = plugin;
        this.festivalManager = festivalManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hanabi.admin")) {
            sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length != 1) {
            sendMessage(sender, "usage");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start" -> {
                if (festivalManager.start()) {
                    Bukkit.broadcastMessage(formatted("start"));
                } else {
                    sendMessage(sender, "already-running");
                }
            }
            case "stop" -> {
                if (festivalManager.stop(true)) {
                    Bukkit.broadcastMessage(formatted("stop"));
                } else {
                    sendMessage(sender, "not-running");
                }
            }
            case "reload" -> {
                plugin.reloadConfig();
                sendMessage(sender, "reload");
            }
            default -> sendMessage(sender, "usage");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> matches = new ArrayList<>();
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    matches.add(sub);
                }
            }
            return matches;
        }
        return List.of();
    }

    private void sendMessage(CommandSender sender, String key) {
        sender.sendMessage(formatted(key));
    }

    private String formatted(String key) {
        String raw = plugin.getConfig().getString("messages." + key, key);
        return ChatColor.translateAlternateColorCodes('&', raw);
    }
}
