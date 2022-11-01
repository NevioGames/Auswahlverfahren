package de.neviogames.auswahlverfahren.utils;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.nglib.utils.io.ErrorHandle;
import de.neviogames.nglib.utils.utility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Configuration {

    @Getter
    private static final Configuration instance = new Configuration();

    private boolean isApplicationPhase;
    private String eventName;
    private String eventDate;
    private String eventTimeFrom;
    private String eventTimeTo;
    private boolean denyFormerCandidates;
    private List<String> bannedEvents;

    private String teamSelectMode;
    private Map<String, NGEventTeam> teams;
    private List<NGEventGroup> groups;

    private Map<String, List<NGEventFixedPlayer>> fixedPlayers;
    private List<UUID> forbiddenPlayers;

    // load values from config
    public boolean load() {
        try {
            FileConfiguration configuration = awv.getPlugin(awv.class).getConfig();
            isApplicationPhase = configuration.getBoolean("settings.applicationPhase");
            eventName = configuration.getString("settings.eventName");
            eventDate = configuration.getString("settings.eventDate.date");
            eventTimeFrom = configuration.getString("settings.eventDate.time.from");
            eventTimeTo = configuration.getString("settings.eventDate.time.to");
            denyFormerCandidates = configuration.getBoolean("settings.denyFormerCandidates");
            bannedEvents = configuration.getStringList("settings.bannedEvents");

            teamSelectMode = configuration.getString("settings.teamSelectMode").toLowerCase();
            teams = new HashMap<>();
            configuration.getConfigurationSection("settings.teams").getKeys(false).forEach(team -> {
                if (!configuration.getBoolean("settings.teams."+team+".enabled")) return;
                teams.put(team, new NGEventTeam(
                        team,
                        configuration.getString("settings.teams." + team + ".display"),
                        configuration.getString("settings.teams." + team + ".color")
                ));
            });

            groups = new ArrayList<>();
            configuration.getConfigurationSection("settings.groups").getKeys(false).forEach(group -> {
                if (!configuration.getBoolean("settings.groups."+group+".enabled")) return;
                if (!utility.isNumber(group)) throw new NumberFormatException();
                groups.add(new NGEventGroup(
                        Integer.parseInt(group),
                        configuration.getInt("settings.groups." + group + ".groupSize"),
                        ChatColor.translateAlternateColorCodes('&', configuration.getString("settings.groups." + group + ".selectionText"))
                ));
            });

            fixedPlayers = new HashMap<>();
            configuration.getConfigurationSection("settings.fixedPlayers").getKeys(false).forEach(player -> {
                if (!configuration.getBoolean("settings.fixedPlayers."+player+".enabled")) return;
                final String team = configuration.getString("settings.fixedPlayers." + player + ".team");

                List<NGEventFixedPlayer> fp = fixedPlayers.getOrDefault(team, new ArrayList<>());
                fp.add(new NGEventFixedPlayer(
                        UUID.fromString(player),
                        team,
                        configuration.getInt("settings.fixedPlayers." + player + ".group")
                ));
                fixedPlayers.put(team, fp);
            });

            forbiddenPlayers = new ArrayList<>();
            configuration.getStringList("settings.forbiddenPlayers").forEach(player ->
                    forbiddenPlayers.add(UUID.fromString(player)));

            return true;
        } catch (Throwable t) {
            ErrorHandle.error(ErrorHandle.cs, t, awv.getPrefix());
            return false;
        }
    }

    public int getTeamSelectModeId() {
        switch (this.teamSelectMode) {
            case "rank":
                return -1;
            case "perm":
                return 1;
            case "select":
                return 2;
            default: // = "auto"
                return 0;
        }
    }

}
