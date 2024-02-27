package discord.commands;

import java.util.Map;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import discord.DiscordCommand;

import scanner.HabboScanner;

import game.console.ConsoleCommand;
import game.console.commands.EnergySavingConsoleCommand;
import game.console.commands.start.StartConsoleCommand;
import game.console.commands.start.modes.StartBotInActiveRoomsMode;

public class InfoDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        boolean isRoomFurniActiveEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getBotProperties().getProperty("room_furni_active.enabled"));

        Map<String, ConsoleCommand> commands = HabboScanner.getInstance().getConsoleHandlers().getCommands();

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand) commands.get(":energy_saving");
        boolean energySavingMode = energySavingConsoleCommand.getEnergySavingMode();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(":start");
        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

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
        String status = isActive ? "abilitato" : "disattivato";

        return variableName + " è " + status + "\n";
    }
}
