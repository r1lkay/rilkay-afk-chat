package com.afk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements CommandExecutor, TabCompleter {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private AFKManager afkManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.afkManager = new AFKManager(this);

        getServer().getPluginManager().registerEvents(new AFKListener(afkManager), this);

        if (getCommand("rafk") != null) {
            getCommand("rafk").setExecutor(this);
            getCommand("rafk").setTabCompleter(this);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                afkManager.checkAFK();
            }
        }.runTaskTimer(this, 20L, 20L);

        getLogger().info("AFK-Chat started successfully!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("rilkayafk.reload")) {
                sender.sendMessage(miniMessage.deserialize("<#f5dda2>У вас нет прав!"));
                return true;
            }

            reloadConfig();
            afkManager.reloadValues(); 
            
            sender.sendMessage(miniMessage.deserialize("<#f5dda2>Configuration reloaded successfully!"));
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            if (sender.hasPermission("rilkayafk.reload")) {
                completions.add("reload");
            }
        }
        
        return completions;
    }

    @Override
    public void onDisable() {
        getLogger().info("AFK-Chat is stopped");
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}