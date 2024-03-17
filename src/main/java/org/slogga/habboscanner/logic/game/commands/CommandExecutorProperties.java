package org.slogga.habboscanner.logic.game.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import gearth.protocol.HMessage;

import lombok.Data;

@Data
public class CommandExecutorProperties {
    private HMessage message;
    private String messageText;
    private int userId;

    SlashCommandInteractionEvent event;
}
