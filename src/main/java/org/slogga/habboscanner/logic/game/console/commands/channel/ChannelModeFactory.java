package org.slogga.habboscanner.logic.game.console.commands.channel;

import java.util.*;
import java.util.function.Supplier;

import org.slogga.habboscanner.logic.game.console.commands.channel.modes.*;

import org.slogga.habboscanner.models.enums.MessageChannel;

public class ChannelModeFactory {
    private static final Map<MessageChannel, Supplier<IChannelMode>> channelModeMap = new HashMap<>();

    static {
        channelModeMap.put(MessageChannel.CHAT, ChatChannelMode::new);
        channelModeMap.put(MessageChannel.CONSOLE, ConsoleChannelMode::new);
    }

    public static IChannelMode getChanelModeStrategy(MessageChannel channel) {
        Supplier<IChannelMode> modeSupplier = channelModeMap.get(channel);

        if (modeSupplier == null) return null;

        return modeSupplier.get();
    }
}
