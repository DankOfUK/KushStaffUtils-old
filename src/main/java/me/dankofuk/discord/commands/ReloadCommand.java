package me.dankofuk.discord.commands;

import me.dankofuk.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class ReloadCommand  extends ListenerAdapter {
    private static DiscordBot jda;
    public FileConfiguration config;

    public ReloadCommand(DiscordBot jda) {
        this.jda = jda;
    }

    public void onMessageReceived(MessageReceivedEvent event) {

        String message = event.getMessage().getContentRaw();

        if (message.equalsIgnoreCase(jda.discordBotCommandPrefix+ "reload")) {
            String botAdminRoleId = jda.config.getString("botAdminRoleId");
            if (!event.getMember().getRoles().contains(event.getGuild().getRoleById(botAdminRoleId)));
            EmbedBuilder noPerms = new EmbedBuilder();
            noPerms.setTitle("__**Error 404**__");
            noPerms.setDescription("> You do not have permission to use this command!");
            noPerms.setFooter("Test");
            event.getChannel().sendMessageEmbeds(noPerms.build()).queue();
            return;
        }

        // Reload Bot Config
        Bukkit.getScheduler().getPendingTasks().stream().filter(task -> task.getOwner() == jda).forEach(task -> task.cancel());
        jda.reloadBot(jda.discordBotToken, jda.discordBotEnabled, jda.discordBotCommandPrefix, jda.discordBotActivity);
    }

}
