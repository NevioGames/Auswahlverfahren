package de.neviogames.auswahlverfahren.utils;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.nglib.utils.io.ErrorHandle;
import de.neviogames.nglib.utils.utility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Configuration {

    @Getter
    private static final Configuration instance = new Configuration();

    private boolean isApplicationPhase;
    private String eventName;
    private boolean denyFormerCandidates;

    private String teamSelectMode;
    private Map<String, NGEventTeam> teams;
    private List<NGEventGroup> groups;

    private Map<String, List<NGEventFixedPlayer>> fixedPlayers;

    // load values from config
    public boolean load() {
        try {
            FileConfiguration configuration = awv.getPlugin(awv.class).getConfig();
            isApplicationPhase = configuration.getBoolean("settings.applicationPhase");
            eventName = configuration.getString("settings.eventName");
            denyFormerCandidates = configuration.getBoolean("settings.denyFormerCandidates");

            teamSelectMode = configuration.getString("settings.teamSelectMode").toLowerCase();
            configuration.getConfigurationSection("settings.teams").getKeys(false).forEach(team -> {
                if (!configuration.getBoolean("settings.teams."+team+".enabled")) return;
                teams.put(team, new NGEventTeam(
                        team,
                        configuration.getString("settings.teams." + team + ".display"),
                        configuration.getString("settings.teams." + team + ".color")
                ));
            });

            configuration.getConfigurationSection("settings.groups").getKeys(false).forEach(group -> {
                if (!configuration.getBoolean("settings.groups."+group+".enabled")) return;
                if (!utility.isNumber(group)) throw new NumberFormatException();
                groups.add(new NGEventGroup(
                        Integer.parseInt(group),
                        configuration.getInt("settings.groups." + group + ".groupSize"),
                        configuration.getString("settings.groups." + group + ".selectionText")
                ));
            });

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
