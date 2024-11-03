package me.muteplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Muteplugin extends JavaPlugin {

    @Override
    public void onEnable() {
        MuteCommand muteCommand = new MuteCommand(this);
        this.getCommand("mute").setExecutor(muteCommand);

        // Register the chat listener
        getServer().getPluginManager().registerEvents(new ChatListener(muteCommand), this);

        getLogger().info("Muteplugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Muteplugin has been disabled!");
    }

    public void notifyStaff(String message) {
        // Sends a message to all players with the 'muteplugin.staff' permission
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("muteplugin.staff"))
                .forEach(staff -> staff.sendMessage(message));
    }
}
