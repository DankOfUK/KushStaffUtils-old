package me.dankofuk;

import me.clip.placeholderapi.metrics.bukkit.Metrics;
import me.dankofuk.discord.DiscordBot;
import me.dankofuk.utils.ColorUtils;
import net.dv8tion.jda.api.JDA;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin implements Listener {
    private JDA jda;
    private Plugin plugin;
    private DiscordBot discordBot;
    private FileConfiguration config;

    // Instance
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    // onEnable
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        instance = this;

        // BStats
        boolean bStatsEnabled = config.getBoolean("bStatsEnabled");
        if (config.getBoolean("bStatsEnabled")) {
            int pluginId = 18185;
            Metrics metrics = new Metrics(this, pluginId);
        } else {
            getLogger().info("[KushStaffUtils-v2] bStats disabled - Will not track plugin data!");
        }

        // No Permission String
        String noPermissionMessage = config.getString("noPermissionMessage");
        // Discord Bot
        if (config.getBoolean("bot.botEnabled")) {
            if (config.getString("bot.botToken") == null || config.getString("bot.botToken").isEmpty()) {
                getLogger().info("[KushStaffUtils-v2 - Discord Bot] No bot token found. Bot initialization skipped.");
                return;
            }

            discordBot = new DiscordBot(config.getString("bot.botToken"), config.getBoolean("bot.botEnabled"), config.getString("bot.botCommandPrefix"), config.getString("bot.botActivityMessage"), this);
            try {
                discordBot.startBot();
                getLogger().info("[KushStaffUtils-v2 - Discord Bot] Starting Discord Bot...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            getLogger().info("[KushStaffUtils-v2 - Discord Bot] Bot is disabled. Skipping initialization...");
        }

        getLogger().info("[KushStaffUtils-v2] Plugin has been enabled");
    }


    // BStats Method
    private void bStatsEnabled(boolean bStatsEnabled) {
    }

    // onDisable
    public void onDisable() {
        // stop Discord bot
        discordBot.stopBot(); // Stops Discord Bot
        getLogger().info("[KushStaffUtils-v2] Plugin has been disabled!");
    }

    // Reload Command
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ksu")) {
            if (!sender.hasPermission("ksu.reload")) {
                sender.sendMessage(ColorUtils.translateColorCodes(Objects.requireNonNull(getConfig().getString("noPermissionMessage"))));
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reloadConfigOptions();
                sender.sendMessage(ColorUtils.translateColorCodes(getConfig().getString("reloadConfig")));
                return true;
            }
            return false;
        }
        return false;
    }


    public void reloadConfigOptions() {
        reloadConfig();
        FileConfiguration config = getConfig();
        instance = this;

        // No Permission Message
        String noPermissionMessage = config.getString("noPermissionMessage");
        String reloadConfig = config.getString("reloadConfig");

        // Reload Discord Bot
        discordBot.reloadBot(config.getString("bot.botToken"), config.getBoolean("bot.botEnabled"), config.getString("bot.botCommandPrefix"), config.getString("bot.botActivityMessage"));

        getLogger().info("[KushStaffUtils-v2] Config options have been reloaded!");

    }

}