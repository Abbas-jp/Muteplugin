package me.muteplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class MuteCommand implements CommandExecutor {

    private final Muteplugin plugin;
    private final HashMap<UUID, Long> mutedPlayers = new HashMap<>();

    public MuteCommand(Muteplugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("mute")) {
            return handleMuteCommand(sender, args);
        } else if (label.equalsIgnoreCase("unmute")) {
            return handleUnmuteCommand(sender, args);
        }
        return false;
    }

    private boolean handleMuteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("muteplugin.mute")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /mute <player> <duration in seconds>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        // Check if the player is already muted and exit early if they are
        if (isPlayerMuted(target)) {
            sender.sendMessage(ChatColor.RED + "That player is already muted.");
            return true;
        }

        try {
            int duration = Integer.parseInt(args[1]);

            mutePlayer(target, duration);
            sender.sendMessage(ChatColor.GREEN + target.getName() + " has been muted for " + duration + " seconds.");
            plugin.notifyStaff(ChatColor.RED + "🔇 " + target.getName() + " has been muted by " + sender.getName() +
                    " for " + duration + " seconds.");

            new BukkitRunnable() {
                @Override
                public void run() {
                    unmutePlayer(target);
                    plugin.notifyStaff(ChatColor.GREEN + "🔔 " + target.getName() + " has been automatically unmuted after " + duration + " seconds.");
                }
            }.runTaskLater(plugin, duration * 20L);  // Convert seconds to ticks

        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Please enter a valid number for the duration.");
        }

        return true;
    }

    private boolean handleUnmuteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("muteplugin.unmute")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unmute <player>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        // Check if the player is muted
        if (!isPlayerMuted(target)) {
            sender.sendMessage(ChatColor.RED + "That player is not currently muted.");
            return true;
        }

        // Unmute the player manually
        unmutePlayer(target);
        sender.sendMessage(ChatColor.GREEN + target.getName() + " has been unmuted.");
        plugin.notifyStaff(ChatColor.GREEN + "🔔 " + target.getName() + " has been manually unmuted by " + sender.getName() + ".");

        return true;
    }

    private void mutePlayer(Player player, int duration) {
        mutedPlayers.put(player.getUniqueId(), System.currentTimeMillis() + (duration * 1000L));
        player.sendMessage(ChatColor.RED + "You have been muted for " + duration + " seconds.");
    }

    private void unmutePlayer(Player player) {
        mutedPlayers.remove(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You have been unmuted.");
    }

    public boolean isPlayerMuted(Player player) {
        return mutedPlayers.containsKey(player.getUniqueId()) &&
                mutedPlayers.get(player.getUniqueId()) > System.currentTimeMillis();
    }
}
