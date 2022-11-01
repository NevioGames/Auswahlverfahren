package de.neviogames.auswahlverfahren.utils;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.nglib.utils.ds.DataSource;
import de.neviogames.nglib.utils.io.ErrorHandle;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"deprecation", "unused"})
public class database {

    public static void createTable() {
        DataSource.createTable("CREATE TABLE IF NOT EXISTS Auswahlevent (" +
                "UUID VARCHAR(50), " +
                "house VARCHAR(25), " +
                "contact VARCHAR(120), " +
                "time BIGINT, " +
                "whitelist INT)");
    }

    public static void add(NGEventCandidate candidate) {
        DataSource.update("INSERT INTO Auswahlevent(UUID, house, contact, time, whitelist) VALUES ('"+candidate.getUniqueId().toString()+"', '"+candidate.getTeam().getName().toUpperCase()+"', '"+candidate.getContact()+"', '"+candidate.getTimestamp()+"', '"+candidate.getWhitelistId()+"')");
    }

    public static void remove(UUID uuid) {
        DataSource.delete("Auswahlevent", uuid);
    }

    public static boolean exist(UUID uuid) {
        return DataSource.existsValue("Auswahlevent", uuid);
    }

    public static String getPlayerTeam(UUID uuid) {
        return DataSource.getStringResult("player",  "RANK", uuid);
    }

    public static void setWhitelist(UUID uuid, int type) {
        DataSource.edit("Auswahlevent", "whitelist", type, uuid);
    }


    public static ArrayList<UUID> getAllMembers(NGEventTeam team) {
        ArrayList<UUID> list = new ArrayList<>();
        try {
            @Cleanup Connection con = DataSource.hikari.getConnection();
            @Cleanup PreparedStatement st = con.prepareStatement("SELECT UUID FROM Auswahlevent WHERE house='"+team.getName()+"' AND whitelist='0'");
            @Cleanup ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(UUID.fromString(rs.getString("UUID")));
            }
        } catch (Throwable e) {
            ErrorHandle.error(ErrorHandle.cs, e, awv.getPrefix());
        }
        return list;
    }

    public static void createTableFormerEventCandidates() {
        DataSource.createTable("CREATE TABLE IF NOT EXISTS FormerEventCandidates (" +
                "UUID VARCHAR(50), " +
                "house VARCHAR(25), " +
                "time BIGINT, " +
                "event VARCHAR(50), " +
                "FreigegebenAb VARCHAR(255))");
    }

    public static boolean isFormerCandidate(UUID uuid) {
        return DataSource.existsValue("FormerEventCandidates", uuid);
    }

    public static String getFormerCandidateEvent (UUID uuid) {
        if (Util.isNullOrEmpty(Configuration.getInstance().getBannedEvents())) return null;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT event FROM FormerEventCandidates WHERE UUID='").append(uuid).append("'");
        boolean first = true;
        for (String event : Configuration.getInstance().getBannedEvents()) {
            if (first) {
                sql.append(" AND (event='").append(event).append("'");
                first = false;
            } else {
                sql.append(" OR event='").append(event).append("'");
            }
        }
        sql.append(")");
        try {
            @Cleanup Connection con = DataSource.hikari.getConnection();
            @Cleanup PreparedStatement st = con.prepareStatement(sql.toString());
            @Cleanup ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getString("event");
            }
        } catch (Throwable e) {
            ErrorHandle.error(ErrorHandle.cs, e, awv.getPrefix());
        }
        return null;
    }

    public static ArrayList<UUID> getAllFormerCandidates(NGEventTeam team) {
        List<String> list = DataSource.getStringList("UUID", "FormerEventCandidates", "house", team.getName());
        ArrayList<UUID> outList = new ArrayList<>();
        for(String s : list) {
            outList.add(UUID.fromString(s));
        }
        return outList;
    }

}

