package org.slogga.habboscanner.discord.commands;

import java.util.Map;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.IDiscordCommand;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.IConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.EnergySavingConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.modes.StartBotInActiveRoomsMode;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;
import org.slogga.habboscanner.models.CommandKeys;

public class InfoDiscordCommand implements IDiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        boolean isRoomFurniActiveEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot").getProperty("room_furni_active.enabled"));

        Map<String, IExecuteCommand> commands = CommandFactory.commandExecutorInstance.getCommands();

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand) commands.get(CommandKeys.ENERGY_SAVING.getKey());
        boolean energySavingMode = energySavingConsoleCommand.getEnergySavingMode();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(CommandKeys.START.getKey());
        boolean isBotRunning = startConsoleCommand.isBotRunning();

        StartBotInActiveRoomsMode startBotInActiveRoomsMode = (StartBotInActiveRoomsMode)
                startConsoleCommand.getStartModes().get("bot.in.active.rooms");
        boolean isProcessingActiveRooms = startBotInActiveRoomsMode.getIsProcessingActiveRooms();

        String statusMessage = printStatus("isRoomFurniActiveEnabled", isRoomFurniActiveEnabled) +
                printStatus("energySavingMode", energySavingMode) +
                printStatus("isBotRunning", isBotRunning) +
                printStatus("isProcessingActiveRooms", isProcessingActiveRooms) +
                "----------------------";

        event.reply(statusMessage).queue();
    }

    private String printStatus(String variableName, boolean isActive) {
        String status = isActive ? "enabled" : "disabled";

        return variableName + " is " + status + "\n";
    }
}
