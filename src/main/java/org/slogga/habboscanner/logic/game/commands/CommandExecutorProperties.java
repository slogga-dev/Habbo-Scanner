package org.slogga.habboscanner.logic.game.commands;

import lombok.Data;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import gearth.protocol.HMessage;

@Data
public class CommandExecutorProperties {
    private HMessage message;
    private String messageText;
    private int userId;

    SlashCommandInteractionEvent event;
}
