package de.neviogames.auswahlverfahren.utils;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
public class NGEventTeam {

    private final String name;
    private final String displayName;
    private final String color;

    public NGEventTeam(String name, String displayName, String color) {
        this.name = name;
        this.displayName = displayName;
        this.color = ChatColor.translateAlternateColorCodes('&', color);
    }

    public static NGEventTeam getTeam(Player player) {
        if (Util.isNullOrEmpty(Configuration.getInstance().getTeams())) return null;
        switch (Configuration.getInstance().getTeamSelectModeId()) {
            case -1: // Rank (dagilp.net)
                return getNGEventTeamByRank(player);
            case 0: // Auto
                return getNGEventTeamByAuto();
            case 1: // Perm
                return getNGEventTeamByPerm(player);
            case 2: // Select
                return null;
            default:
                throw new NullPointerException("SelectModeId not found");
        }
    }

    private static NGEventTeam getNGEventTeamByRank(Player player) {
        return Configuration.getInstance().getTeams().get(database.getPlayerTeam(player.getUniqueId()).toLowerCase());
    }

    private static NGEventTeam getNGEventTeamByAuto() {
        final int random = Randomizer.fairRoundedRandom(1, Configuration.getInstance().getTeams().size());
        int counter = 0;
        for (String key : Configuration.getInstance().getTeams().keySet()) {
            counter++;
            if (counter != random) continue;
            return Configuration.getInstance().getTeams().get(key);
        }
        return null;
    }

    private static NGEventTeam getNGEventTeamByPerm(Player player) {
        for (String team : Configuration.getInstance().getTeams().keySet()) {
            if (!player.hasPermission("nge.team."+team)) continue;
            return Configuration.getInstance().getTeams().get(team);
        }
        return null;
    }

}
