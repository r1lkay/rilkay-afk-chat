package com.afk;

import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class AFKManager {

    private final Main plugin;
    private final HashMap<UUID, Long> lastActivity = new HashMap<>();
    private final HashSet<UUID> afkPlayers = new HashSet<>();

    public AFKManager(Main plugin) {
        this.plugin = plugin;
    }

    public void updateActivity(Player player) {
        UUID uuid = player.getUniqueId();

        if (afkPlayers.contains(uuid)) {
            afkPlayers.remove(uuid);
            broadcastMessage("messages.returned-afk", player);
        }

        lastActivity.put(uuid, System.currentTimeMillis());
    }

    public void checkAFK() {
        long threshold = plugin.getConfig().getLong("afk-time") * 1000;
        long now = System.currentTimeMillis();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();

            if (afkPlayers.contains(uuid)) continue;

            if (!lastActivity.containsKey(uuid)) {
                lastActivity.put(uuid, now);
                continue;
            }

            if (now - lastActivity.get(uuid) > threshold) {
                afkPlayers.add(uuid);
                broadcastMessage("messages.went-afk", player);
            }
        }
    }

    private void broadcastMessage(String path, Player player) {
        String raw = plugin.getConfig().getString(path, "Message missing!");
        String formatted = raw.replace("{player}", player.getName());

        Component message = plugin.getMiniMessage().deserialize(formatted);
        plugin.getServer().broadcast(message);
    }

    public void removePlayer(UUID uuid) {
        lastActivity.remove(uuid);
        afkPlayers.remove(uuid);
    }

    public void reloadValues() {
        lastActivity.clear();
        afkPlayers.clear();
        long now = System.currentTimeMillis();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            lastActivity.put(player.getUniqueId(), now);
        }
    }
}