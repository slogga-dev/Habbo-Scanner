package org.slogga.habboscanner.handlers;

import java.util.*;
import java.util.stream.*;

import gearth.extensions.parsers.HEntity;
import gearth.protocol.HMessage;

import org.slogga.habboscanner.logic.game.BaseEntityFactory;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.models.entities.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.utils.*;

public class UserHandlers {
    private List<BaseEntity> entities;

    public void onUsers(HMessage message) {
        boolean isUserScannerActive = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot").getProperty("user.active"));

        int roomId = HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers().getRoomId();

        if (!isUserScannerActive || roomId == 0) return;

        entities = Stream.of(HEntity.parse(message.getPacket()))
                .map(entity -> BaseEntityFactory.createEntity(entity, roomId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        entities.forEach(BaseEntity::processEntity);
    }

    public void onChat(HMessage message) {
        boolean isBotEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot").getProperty("bot.enabled"));

        boolean isResponseToChatEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot").getProperty("response.to.chat.enabled"));

        if (!isBotEnabled || !isResponseToChatEnabled) return;

        int entityIndex = message.getPacket().readInteger();
        String messageText = message.getPacket().readString();

        if (entities == null) return;

        String botName = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot").getProperty("bot.name");

        HabboEntity botEntity = EntityFinder.findHabboBotEntity(entities, botName);

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        if (botEntity == null) {
            String unsetBotNameMessage = HabboScanner.getInstance().getConfigurator()
                    .getProperties().get("message").getProperty("unset.bot.name.message");
            HabboActions.sendPrivateMessage(consoleUserId, unsetBotNameMessage);

            return;
        }

        boolean isBotEntity = entityIndex == botEntity.getIndex();

        if (isBotEntity) return;

        String phrasesThatTriggerBotReaction = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message").getProperty("phrases.that.trigger.bot.reaction");

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
