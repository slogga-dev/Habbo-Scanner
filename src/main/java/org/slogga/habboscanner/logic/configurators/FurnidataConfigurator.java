package org.slogga.habboscanner.logic.configurators;
import lombok.Data;
import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.dao.mysql.items.ItemsDAO;
import org.slogga.habboscanner.logic.DefaultValues;
import org.slogga.habboscanner.models.furnidata.Furnidata;
import org.slogga.habboscanner.utils.JsonUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@Data
public class FurnidataConfigurator implements IConfigurator{
    private Map<String, Map<String, String>> items;
    @Override
    public void setupConfig() throws RuntimeException {
        setupFurniData();
        setupItems();
    }
    private void setupFurniData() throws RuntimeException{
        String hotelDomain = HabboScanner
                .getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot")
                .getProperty("hotel.domain");

        if (!DefaultValues.getInstance().getValidDomains().contains(hotelDomain)) {
            DefaultValues.getInstance().getLogger().error("The hotel domain is incorrect.");

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
    private void setupItems(){
        try{
            items = ItemsDAO.fetchItems();
        }catch (IOException | SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
    private String getFurnidataURL(String hotelDomain) {
        if (hotelDomain.equals("s2"))
            return "https://sandbox.habbo.com/gamedata/furnidata_json/1";

        return "https://www.habbo." + hotelDomain + "/gamedata/furnidata_json/1";
    }
}
