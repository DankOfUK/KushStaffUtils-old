package me.dankofuk.discord;

import me.dankofuk.Main;
import me.dankofuk.discord.commands.ReloadCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.util.List;

public class DiscordBot extends ListenerAdapter {
    public String discordBotToken;
    public boolean discordBotEnabled;
    public String discordBotCommandPrefix;
    public Plugin discordBotTask;
    public String discordBotActivity;
    public FileConfiguration config;

    public JDA jda;
    public Main main;

    public DiscordBot(String discordBotToken, boolean discordBotEnabled, String discordBotCommandPrefix, String discordBotActivity, Main main) {
        this.discordBotToken = discordBotToken;
        this.discordBotCommandPrefix = discordBotCommandPrefix;
        this.discordBotEnabled = discordBotEnabled;
        this.discordBotActivity = discordBotActivity;
        this.main = main;
    }

    // DiscordBot Instance
    public JDA getJdaInstance() {
        return jda;
    }

    public void startBot() throws InterruptedException {

        if (!discordBotEnabled) {
            return;
        }

        // Creates the Discord Bot
        jda = JDABuilder.createDefault(discordBotToken)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(this)
                .setActivity(Activity.playing(discordBotActivity))
                .build()
                .awaitReady();

        // Register Discord Events

        // Reload Command
        jda.addEventListener(new ReloadCommand(this));
    }

    // Method for stopping the Discord Bot
    public void stopBot() {
        if (jda != null) {
            jda.shutdown();
            // Cancel any tasks registered by the bot
            Bukkit.getScheduler().getPendingTasks().stream()
                    .filter(task -> task.getOwner() == discordBotTask)
                    .forEach(task -> task.cancel());
        }
    }

    // Help Command
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String message = event.getMessage().getContentRaw();
        // Help Command
        if (message.equalsIgnoreCase(discordBotCommandPrefix + "help")) {
            EmbedBuilder helpEmbed = new EmbedBuilder();
            helpEmbed.setColor(Color.RED);
            helpEmbed.setTitle("__`Help Page 1/1`__");
            helpEmbed.setDescription("__**Command Prefix:**__ `" + discordBotCommandPrefix + "`");
            helpEmbed.addField("Help Commands", "help", false);
            helpEmbed.setFooter("Help Page 1/1 - KushStaffUtils v2");
            event.getChannel().sendMessageEmbeds(helpEmbed.build()).queue();
        }
    }


    // Reloads the Discord Bot configuration and restarts it
    public void reloadBot(String discordBotToken, boolean discordBotEnabled, String discordBotCommandPrefix, String discordBotActivity) {
        stopBot(); // Stop the bot if it's already running

        // Update the configuration with the new values
        this.discordBotToken = discordBotToken;
        this.discordBotEnabled = discordBotEnabled;
        this.discordBotCommandPrefix = discordBotCommandPrefix;
        this.discordBotActivity = discordBotActivity;

        try {
            startBot(); // Start the bot with the new configuration
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}