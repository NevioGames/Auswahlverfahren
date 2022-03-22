package de.neviogames.auswahlverfahren.utils;


import de.neviogames.nglib.utils.ds.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"deprecation", "unused"})
public class database {

    public static void createTable() {
        DataSource.createTable("CREATE TABLE IF NOT EXISTS Auswahlevent (UUID VARCHAR(50), house VARCHAR(25), contact VARCHAR(120), time BIGINT, whitelist INT)");
    }

    public static void add(UUID uuid, House house, String contact) {
        DataSource.update("INSERT INTO Auswahlevent(UUID, house, contact, time, whitelist) VALUES ('"+uuid.toString()+"', '"+house.name()+"', '"+contact+"', '"+System.currentTimeMillis()+"', '0')");

    }
    public static void remove(UUID uuid) {
        DataSource.delete("Auswahlevent", uuid);
    }

    public static boolean exist(UUID uuid) {
        return DataSource.existsValue("Auswahlevent", uuid);
    }

    public static String getPlayerHouse(UUID uuid) {
        return DataSource.getStringResult("player",  "RANK", uuid);
    }

    public static void setWhitelist(UUID uuid, int type) {
        DataSource.edit("Auswahlevent", "whitelist", type, uuid);
    }


    public static ArrayList<UUID> getAllMembers(House house) {
        List<String> list = DataSource.getStringList("UUID", "Auswahlevent", "house", house.name());
        ArrayList<UUID> outList = new ArrayList<>();
        for(String s : list) {
            outList.add(UUID.fromString(s));
        }
        return outList;
    }


    public static void createTableFormerEventCandidates() {
        DataSource.createTable("CREATE TABLE IF NOT EXISTS FormerEventCandidates (UUID VARCHAR(50), house VARCHAR(25), time BIGINT, event VARCHAR(50), FreigegebenAb VARCHAR(255))");
    }

    public static boolean isFormerCandidate(UUID uuid) {
        return DataSource.existsValue("FormerEventCandidates", uuid);
    }

    public static String getFormerCandidateEvent (UUID uuid) {
        return DataSource.getStringResult("FormerEventCandidates",  "event", uuid);
    }

    public static ArrayList<UUID> getAllFormerCandidates(House house) {
        List<String> list = DataSource.getStringList("UUID", "FormerEventCandidates", "house", house.name());
        ArrayList<UUID> outList = new ArrayList<>();
        for(String s : list) {
            outList.add(UUID.fromString(s));
        }
        return outList;
    }

}

