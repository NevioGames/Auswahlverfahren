package de.neviogames.auswahlverfahren.utils;


import de.neviogames.auswahlverfahren.main.awv;
import de.neviogames.nglib.utils.ds.DataSource;
import de.neviogames.nglib.utils.io.ErrorHandle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"deprecation", "unused"})
public class database {

    public static void createTable() {
        DataSource.createTable("CREATE TABLE IF NOT EXISTS Auswahlevent (UUID VARCHAR(50), house VARCHAR(25), contact VARCHAR(120), time BIGINT, whitelist INT)");
    }

    public static void add(UUID uuid, House house, String contact) {
        DataSource.update("INSERT INTO Auswahlevent(UUID, house, contact, time, whitelist) VALUES ('"+uuid.toString()+"', '"+house.getDB_Name()+"', '"+contact+"', '"+System.currentTimeMillis()+"', '0')");

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

    public static String getContact (UUID uuid) {
        return DataSource.getStringResult("Auswahlevent",  "contact", uuid);
    }

    public static long getTimestamp (UUID uuid) {
        return DataSource.getLongContent("Auswahlevent",  "time", uuid);
    }

    public static void setWhitelist(UUID uuid, int type) {
        DataSource.edit("Auswahlevent", "whitelist", type, uuid);
    }

    public static int getWhitelistType(UUID uuid) {
        return DataSource.getIntContent("Auswahlevent", "whitelist", uuid);
    }

    public static List<UUID> getWhitelistMembers(House house, int type) {
        Connection con = null;
        List<UUID> list = new ArrayList<>();
        try{
            con = DataSource.hikari.getConnection();
            PreparedStatement st = con.prepareStatement("SELECT UUID FROM Auswahlevent WHERE house= '" + house.getDB_Name() + "' AND  whitelist= '" + type + "'");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String toUuid = rs.getString("UUID");
                if(toUuid != null) list.add(UUID.fromString(toUuid));
            }
        } catch (Throwable e) {
            ErrorHandle.error(ErrorHandle.cs, e, awv.getPrefix());
        }
        finally {
            if(con !=null) {
                try {
                    con.close();
                } catch(Throwable e) {
                    ErrorHandle.error(ErrorHandle.cs, e, awv.getPrefix());
                }
            }
        }
        return list;
    }

    public static ArrayList<UUID> getAllMembers(House house) {
        List<String> list = DataSource.getStringList("UUID", "Auswahlevent", "house", house.getDB_Name());
        ArrayList<UUID> outList = new ArrayList<>();
        for(String s : list) {
            outList.add(UUID.fromString(s));
        }
        return outList;
    }


    public static void createTableFormerEventCandidates() {
        DataSource.createTable("CREATE TABLE IF NOT EXISTS FormerEventCandidates (UUID VARCHAR(50), house VARCHAR(25), time BIGINT, event VARCHAR(50))");
    }

    public static boolean isFormerCandidate(UUID uuid) {
        return DataSource.existsValue("FormerEventCandidates", uuid);
    }

    public static void addFormerCandidates(UUID uuid, House house) {
        DataSource.update("INSERT INTO FormerEventCandidates(UUID, house, time, event) VALUES ('"+uuid.toString()+"', '"+house.getDB_Name()+"', '"+System.currentTimeMillis()+"', '"+Configuration.getInstance().getEventName()+"')");
    }

    public static long getFormerCandidateTimestamp (UUID uuid) {
        return DataSource.getLongContent("FormerEventCandidates",  "time", uuid);
    }

    public static String getFormerCandidateHouse (UUID uuid) {
        return DataSource.getStringResult("FormerEventCandidates",  "house", uuid);
    }

    public static String getFormerCandidateEvent (UUID uuid) {
        return DataSource.getStringResult("FormerEventCandidates",  "event", uuid);
    }

    public static ArrayList<UUID> getAllFormerCandidates(House house) {
        List<String> list = DataSource.getStringList("UUID", "FormerEventCandidates", "house", house.getDB_Name());
        ArrayList<UUID> outList = new ArrayList<>();
        for(String s : list) {
            outList.add(UUID.fromString(s));
        }
        return outList;
    }

}

