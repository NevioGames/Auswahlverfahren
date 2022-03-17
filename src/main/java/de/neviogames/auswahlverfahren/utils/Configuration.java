package de.neviogames.auswahlverfahren.utils;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.nglib.utils.io.ErrorHandle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Configuration {

    @Getter
    private static final Configuration instance = new Configuration();

    private boolean isEnabled;
    private boolean dagiPlay;
    private String eventName;
    private boolean denyFormerCandidates;
    private boolean secondTeam;
    private String selectionText1;
    private String selectionText2;
    private int teamSize1;
    private int teamSize2;
    private boolean onlyWizards;

    // load values from config
    public boolean load() {
        try {
            FileConfiguration configuration = awv.getPlugin(awv.class).getConfig();
            isEnabled = configuration.getBoolean("settings.enable");
            dagiPlay = configuration.getBoolean("settings.dagiPlay");
            eventName = configuration.getString("settings.eventName");
            denyFormerCandidates = configuration.getBoolean("settings.denyFormerCandidates");
            secondTeam = configuration.getBoolean("settings.secondTeam");
            selectionText1 = ChatColor.translateAlternateColorCodes('&', configuration.getString("settings.selectionText1"));
            selectionText2 = ChatColor.translateAlternateColorCodes('&', configuration.getString("settings.selectionText2"));
            teamSize1 = configuration.getInt("settings.teamSize1");
            teamSize2 = configuration.getInt("settings.teamSize2");
            onlyWizards = configuration.getBoolean("settings.onlyWizards");

            return true;
        } catch (Throwable t) {
            ErrorHandle.error(ErrorHandle.cs, t, awv.getPrefix());
            return false;
        }

    }

}
