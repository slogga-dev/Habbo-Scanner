package discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface DiscordCommand {
    void execute(SlashCommandInteractionEvent event);
}
