package de.neviogames.auswahlverfahren.utils;


import de.neviogames.nglib.utils.ds.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class database {

    public static void createTable() {
        DataSource.createTable("CREATE TABLE IF NOT EXISTS Auswahlevent (UUID VARCHAR(50), house VARCHAR(25), contact VARCHAR(120), time BIGINT)");
    }

    public static void add(UUID uuid, House house, String contact) {
        //DataSource.update("INSERT INTO Auswahlevent VALUES ('"+uuid.toString()+"',\"UUID\"), ('"+house.getDB_Name()+"',\"house\"),('"+contact+"',\"contact\"),('"+System.currentTimeMillis()+"',\"time\")");
        DataSource.update("INSERT INTO Auswahlevent(UUID, house, contact, time) VALUES ('"+uuid.toString()+"', '"+house.getDB_Name()+"', '"+contact+"', '"+System.currentTimeMillis()+"')");

    }
    public static void remove(UUID uuid) {
        DataSource.delete("Auswahlevent", uuid);
    }

    public static boolean exist(UUID uuid) {
        return DataSource.existsValue("Auswahlevent", uuid);
    }

    public static String getHouse (UUID uuid) {
        return DataSource.getStringResult("player",  "RANK", uuid);
    }

    public static String getContact (UUID uuid) {
        return DataSource.getStringResult("Auswahlevent",  "contact", uuid);
    }

    public static long getTimestamp (UUID uuid) {
        return DataSource.getLongContent("Auswahlevent",  "time", uuid);
    }

    public static ArrayList<UUID> getAllMembers(House house) {
        List<String> list = DataSource.getStringList("UUID", "Auswahlevent", "house", house.getDB_Name());
        ArrayList<UUID> outList = new ArrayList<>();
        for(String s : list) {
            outList.add(UUID.fromString(s));
        }
        return outList;
    }

}

