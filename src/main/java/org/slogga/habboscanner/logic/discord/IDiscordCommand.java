package org.slogga.habboscanner.logic.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface IDiscordCommand {
    void execute(SlashCommandInteractionEvent event);
}
