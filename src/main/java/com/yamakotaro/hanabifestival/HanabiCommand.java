package com.yamakotaro.hanabifestival;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HanabiCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of(
            "start", "stop", "reload", "status", "list", "stats", "gui", "setpoint", "delpoint", "points");

    private final HanabiFestivalPlugin plugin;

    public HanabiCommand(HanabiFestivalPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hanabi.admin")) {
            plugin.getMessageManager().send(sender, "no-permission");
            return true;
        }
        if (args.length == 0) {
            plugin.getMessageManager().send(sender, "usage");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start" -> handleStart(sender, args);
            case "stop" -> handleStop(sender);
            case "reload" -> handleReload(sender);
            case "status" -> handleStatus(sender);
            case "list" -> handleList(sender);
            case "stats" -> handleStats(sender);
            case "gui" -> handleGui(sender);
            case "setpoint" -> handleSetPoint(sender, args);
            case "delpoint" -> handleDelPoint(sender, args);
            case "points" -> handlePoints(sender);
            default -> plugin.getMessageManager().send(sender, "usage");
        }
        return true;
    }

    private void handleStart(CommandSender sender, String[] args) {
        String showId = args.length >= 2 ? args[1] : plugin.getConfig().getString("default-show", "random");
        ShowDefinition show = plugin.getShowManager().get(showId);
        if (show == null) {
            Map<String, String> ph = new HashMap<>();
            ph.put("show", showId);
            plugin.getMessageManager().send(sender, "unknown-show", ph);
            return;
        }
        if (show.getPermission() != null && !show.getPermission().isBlank()
                && !sender.hasPermission(show.getPermission())) {
            Map<String, String> ph = new HashMap<>();
            ph.put("show", show.getDisplayNameColored());
            plugin.getMessageManager().send(sender, "show-no-permission", ph);
            return;
        }
        if (!plugin.getFestivalManager().start(show)) {
            plugin.getMessageManager().send(sender, "already-running");
        }
    }

    private void handleList(CommandSender sender) {
        sender.sendMessage(plugin.getMessageManager().format("list-header"));
        for (String id : plugin.getShowManager().getIds()) {
            ShowDefinition show = plugin.getShowManager().get(id);
            Map<String, String> ph = new HashMap<>();
            ph.put("show", show.getDisplayNameColored());
            ph.put("mode", show.getMode().name());
            sender.sendMessage(plugin.getMessageManager().format("list-entry", ph));
        }
    }

    private void handleStats(CommandSender sender) {
        StatsManager stats = plugin.getStatsManager();
        sender.sendMessage(plugin.getMessageManager().format("stats-header"));

        Map<String, String> totalPh = new HashMap<>();
        totalPh.put("total", String.valueOf(stats.getTotalLaunched()));
        sender.sendMessage(plugin.getMessageManager().format("stats-total", totalPh));

        List<StatsManager.PlayerStat> top = stats.getTopViewers(5);
        Map<String, String> headerPh = new HashMap<>();
        headerPh.put("limit", String.valueOf(top.size()));
        sender.sendMessage(plugin.getMessageManager().format("stats-top-header", headerPh));

        int rank = 1;
        for (StatsManager.PlayerStat stat : top) {
            Map<String, String> ph = new HashMap<>();
            ph.put("rank", String.valueOf(rank++));
            ph.put("name", stat.getName());
            ph.put("watched", String.valueOf(stat.getWatched()));
            sender.sendMessage(plugin.getMessageManager().format("stats-top-entry", ph));
        }
    }

    private void handleStop(CommandSender sender) {
        if (!plugin.getFestivalManager().stop()) {
            plugin.getMessageManager().send(sender, "not-running");
        }
    }

    private void handleReload(CommandSender sender) {
        plugin.reloadAll();
        plugin.getMessageManager().send(sender, "reload");
    }

    private void handleStatus(CommandSender sender) {
        FestivalManager manager = plugin.getFestivalManager();
        if (!manager.isRunning()) {
            plugin.getMessageManager().send(sender, "status-idle");
            return;
        }
        Map<String, String> ph = new HashMap<>();
        ph.put("show", manager.getCurrentShow().getDisplayNameColored());
        ph.put("elapsed", String.valueOf(manager.getElapsedSeconds()));
        plugin.getMessageManager().send(sender, "status-running", ph);
    }

    private void handleGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return;
        }
        plugin.getGuiManager().open(player);
    }

    private void handleSetPoint(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return;
        }
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usage");
            return;
        }
        String name = args[1];
        Location location = player.getLocation();
        plugin.getPointManager().set(name, location);
        Map<String, String> ph = new HashMap<>();
        ph.put("point", name);
        plugin.getMessageManager().send(sender, "point-set", ph);
    }

    private void handleDelPoint(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usage");
            return;
        }
        String name = args[1];
        Map<String, String> ph = new HashMap<>();
        ph.put("point", name);
        if (plugin.getPointManager().remove(name)) {
            plugin.getMessageManager().send(sender, "point-removed", ph);
        } else {
            plugin.getMessageManager().send(sender, "point-not-found", ph);
        }
    }

    private void handlePoints(CommandSender sender) {
        if (plugin.getPointManager().isEmpty()) {
            plugin.getMessageManager().send(sender, "points-empty");
            return;
        }
        sender.sendMessage(plugin.getMessageManager().format("points-list-header"));
        for (LaunchPoint point : plugin.getPointManager().getAll().values()) {
            Map<String, String> ph = new HashMap<>();
            ph.put("point", point.getName());
            ph.put("world", point.getWorldName());
            ph.put("x", String.format("%.1f", point.getX()));
            ph.put("y", String.format("%.1f", point.getY()));
            ph.put("z", String.format("%.1f", point.getZ()));
            sender.sendMessage(plugin.getMessageManager().format("points-list-entry", ph));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {
                return plugin.getShowManager().getIds().stream()
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (args[0].equalsIgnoreCase("delpoint")) {
                return new ArrayList<>(plugin.getPointManager().getNames()).stream()
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }
}
