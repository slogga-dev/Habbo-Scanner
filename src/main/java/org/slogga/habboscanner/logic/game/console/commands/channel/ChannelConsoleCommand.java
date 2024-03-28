package org.slogga.habboscanner.logic.game.console.commands.channel;

import java.util.*;

import lombok.*;

import org.slogga.habboscanner.logic.commands.*;

import org.slogga.habboscanner.models.enums.MessageChannel;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelConsoleCommand extends Command {
    private MessageChannel channel;

    @Override
    public void execute(CommandExecutorProperties properties) {
        String[] arguments = properties.getMessageText().split(" ", 2);

        Optional<String> channelString = Arrays.stream(arguments)
                .skip(1)
                .findFirst();

        if (channelString.isEmpty()) {
            sendMessage("bo sei paxissimo devi decidere su che canale inviare i messaggi !!", properties);

            return;
        }

        boolean isValidArgument = channelString.get().equals("chat") || channelString.get().equals("console");

        if (!isValidArgument) {
            sendMessage("ei ti stai inventando la modalità??!! devi scegliere tra chat e console!!", properties);

            return;
        }

        channel = MessageChannel.fromValue(channelString.get());

        IChannelMode modeStrategy = ChannelModeFactory.getChanelModeStrategy(channel);

        if (modeStrategy == null) return;

        modeStrategy.execute();

        sendMessage("modalità cambiata con successo agaagaha", properties);
    }

    @Override
    public String getDescription() {
        return null;
    }
}
