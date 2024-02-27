package game.entities;

import discord.DiscordWebhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.sql.SQLException;
import java.util.*;

import gearth.extensions.parsers.HEntity;

import database.dao.entities.habbo_users.HabboUsersDAO;
import database.dao.entities.habbo_users.HabboUsersHistoryDAO;
import database.dao.data.DataDAO;
import database.dao.RoomHistoryDAO;
import database.dao.*;

import scanner.HabboScanner;
import services.UserService;

public class HabboEntity extends BaseEntity {
    private static final Logger logger = LoggerFactory.getLogger(HabboEntity.class);

    public HabboEntity(HEntity entity, int roomId) {
        super(entity, roomId);
    }

    @Override
    public void processEntity() {
        ArrayList<HashMap<String, Object>> user;

        try {
            user = HabboUsersDAO.getUserByID(entity.getId());

            RoomHistoryDAO.insertRoomHistory(entity.getId(), roomId);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        if (user.isEmpty()) {
            UserService.insertUser(entity, roomId);

            return;
        }

        HashMap<String, Object> userRow = user.get(0);

        String name = (String) userRow.get("name");
        String motto = Optional.ofNullable((String) userRow.get("motto")).orElse("");
        String gender = Optional.ofNullable((String) userRow.get("gender")).orElse("");
        String look = Optional.ofNullable((String) userRow.get("look")).orElse("");
        int seenTimes = (int) userRow.get("seen_times");

        String mottoInUTF8Format = new String(entity.getMotto()
                .getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        boolean hasNameChanged = !name.equals(entity.getName());
        boolean hasDetailsChanged = !Objects.equals(name, entity.getName()) ||
                !Objects.equals(gender, entity.getGender().toString()) ||
                !Objects.equals(motto, mottoInUTF8Format) ||
                !Objects.equals(look, entity.getFigureId());

        if (hasDetailsChanged) {
            try {
                HabboUsersHistoryDAO.addUserToHistory(entity.getId(), name, motto, gender, look);
            } catch (SQLException | IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        if (hasNameChanged) {
            try {
                String userNameChangeLogMessage = HabboScanner.getInstance()
                        .getMessageProperties().getProperty("user.name.change.log.message");

                userNameChangeLogMessage = userNameChangeLogMessage
                        .replace("%name%", name)
                        .replace("%newName%", entity.getName());

                LogsDAO.insertLog(userNameChangeLogMessage);

                DiscordWebhook.sendDiscordEmbedMessage(name + " ha cambiato nome!", name + " ora si chiama **" +
                                entity.getName() + "** in Habbo IT", 0xffff00,
                        "https://www.habbo.it/habbo-imaging/avatarimage?direction=4&user=" + entity.getName() +
                                "&headonly=1", "https://i.imgur.com/NDlWDSi.png", "Habbo IT", null);

                logger.info(name + " ha cambiato nome, ora si chiama " + entity.getName());

                DataDAO.updateOwner(name, entity.getName());
            } catch (SQLException | IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        UserService.updateUser(entity, mottoInUTF8Format, ++seenTimes, roomId);

        Set<String> staffNames = new HashSet<>(Arrays.asList("Adaara", "Mrs.Phoebe", "Johno",
                "Macklebee", "official_rooms", "singhr", "Morgaine", "Teppo", "Alyx_Staff", "-istanbul-", "Lafollegrenouille"));

        if (!staffNames.contains(entity.getName())) return;

        DiscordWebhook.sendDiscordEmbedMessage("Ho avvistato " + entity.getName() + "!",
                "Ho appena avvistato " + entity.getName() + " nella stanza ID: **" + roomId +
                        "** in Habbo IT", 0xffff00,
                "https://www.habbo.it/habbo-imaging/avatarimage?direction=4&user=" +
                        entity.getName() + "&headonly=1", "https://i.imgur.com/NDlWDSi.png",
                "Habbo IT", null);

        logger.info("Ho avvistato " + entity.getName() + " nella stanza ID: " + roomId);
    }
}