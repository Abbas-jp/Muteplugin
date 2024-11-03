package me.muteplugin;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

public class ChatListener implements Listener {

    private final MuteCommand muteCommand;

    public ChatListener(MuteCommand muteCommand) {
        this.muteCommand = muteCommand;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (muteCommand.isPlayerMuted(player)) {
            player.sendMessage(ChatColor.RED + "You are muted and cannot send messages.");
            event.setCancelled(true);
        }
    }
}
