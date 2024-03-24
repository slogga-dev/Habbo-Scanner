package org.slogga.habboscanner.logic.configurators;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.*;

import lombok.Data;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.dao.mysql.items.ItemsDAO;
import org.slogga.habboscanner.logic.DefaultValues;
import org.slogga.habboscanner.models.furni.Furnidata;
import org.slogga.habboscanner.utils.JsonUtils;

@Data
public class FurnidataConfigurator implements IConfigurator {
    private final Logger logger = LoggerFactory.getLogger(FurnidataConfigurator.class);

    private Map<String, Map<String, String>> items;

    @Override
    public void setupConfig() throws RuntimeException {
        setupFurnidata();
        setupItems();
    }

    private void setupFurnidata() throws RuntimeException{
        String hotelDomain = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("bot").getProperty("hotel.domain");

        if (!DefaultValues.getInstance().getValidDomains().contains(hotelDomain)) {
            logger.error("The hotel domain is incorrect.");

            System.exit(0);
        }

        String furnidataURL = getFurnidataURL(hotelDomain);

        try {
            String furnidataJSON = JsonUtils.fetchJSON(furnidataURL);

            Furnidata.setInstance(furnidataJSON);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void setupItems() {
        try {
            items = ItemsDAO.fetchItems();
        } catch (IOException | SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private String getFurnidataURL(String hotelDomain) {
        if (hotelDomain.equals("s2"))
            return "https://sandbox.habbo.com/gamedata/furnidata_json/1";

        return "https://www.habbo." + hotelDomain + "/gamedata/furnidata_json/1";
    }
}
