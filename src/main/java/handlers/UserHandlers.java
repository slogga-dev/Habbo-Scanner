package handlers;

import java.util.*;
import java.util.stream.*;

import gearth.extensions.parsers.HEntity;
import gearth.protocol.HMessage;

import game.entities.*;

import scanner.HabboScanner;

import utils.*;

public class UserHandlers {
    private List<BaseEntity> entities;

    public void onUsers(HMessage message) {
        boolean isUserScannerActive = Boolean.parseBoolean(HabboScanner.getInstance()
                .getBotProperties().getProperty("user.scanner.active"));

        int roomId = HabboScanner.getInstance().getRoomInfoHandlers().getRoomId();

        if (!isUserScannerActive || roomId == 0) return;

        entities = Stream.of(HEntity.parse(message.getPacket()))
                .map(entity -> BaseEntityFactory.createEntity(entity, roomId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        entities.forEach(BaseEntity::processEntity);
    }

    public void onChat(HMessage message) {
        boolean isBotEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getBotProperties().getProperty("bot.enabled"));

        boolean isResponseToChatEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getBotProperties().getProperty("response.to.chat.enabled"));

        if (!isBotEnabled || !isResponseToChatEnabled) return;

        int entityIndex = message.getPacket().readInteger();
        String messageText = message.getPacket().readString();

        if (entities == null) return;

        String botName = HabboScanner.getInstance().getBotProperties().getProperty("bot.name");

        HabboEntity botEntity = EntityFinder.findHabboBotEntity(entities, botName);

        int consoleUserId = HabboScanner.getInstance().getConsoleHandlers().getUserId();

        if (botEntity == null) {
            HabboScanner.getInstance().sendPrivateMessage(consoleUserId,
                    "bo sei paxo devi impostare il mio nome nelle impostazioni nn funzionerà mai così agahagah");

            return;
        }

        boolean isBotEntity = entityIndex == botEntity.getIndex();

        if (isBotEntity) return;

        String phrasesThatTriggerBotReaction = HabboScanner.getInstance()
                .getMessageProperties().getProperty("phrases.that.trigger.bot.reaction");

        String[] phrasesThatTriggerBotReactionArray = phrasesThatTriggerBotReaction.split("---");

        for (String phrase : phrasesThatTriggerBotReactionArray) {
            int delimiterIndex = phrase.indexOf(":");

            String key = phrase.substring(0, delimiterIndex);
            String value = phrase.substring(delimiterIndex + 1);

            Map<Character, Runnable> actions = ActionMapGenerator.generateActionMapFromValue(value);

            if (!messageText.contains(key)) continue;

            char actionKey = value.charAt(0);

            if (!actions.containsKey(actionKey)) return;

            Runnable action = actions.get(actionKey);

            action.run();
        }
    }
}
