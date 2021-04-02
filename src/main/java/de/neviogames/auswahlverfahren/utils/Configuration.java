package de.neviogames.auswahlverfahren.utils;

import de.neviogames.auswahlverfahren.main.awv;
import de.neviogames.nglib.utils.io.ErrorHandle;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public final class Configuration {

    private static final Configuration instance = new Configuration();

    private String SERVER_NAME;

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


    public boolean load() {
        try {
            FileConfiguration configuration = awv.getPlugin(awv.class).getConfig();
            SERVER_NAME = configuration.getString(ConfigEnum.SERVER_NAME.getPath());

            isEnabled = configuration.getBoolean(ConfigEnum.isEnabled.getPath());
            dagiPlay = configuration.getBoolean(ConfigEnum.dagiPlay.getPath());
            eventName = configuration.getString(ConfigEnum.eventName.getPath());
            denyFormerCandidates = configuration.getBoolean(ConfigEnum.denyFormerCandidates.getPath());
            secondTeam = configuration.getBoolean(ConfigEnum.secondTeam.getPath());
            selectionText1 = ChatColor.translateAlternateColorCodes('&', configuration.getString(ConfigEnum.selectionText1.getPath()));
            selectionText2 = ChatColor.translateAlternateColorCodes('&', configuration.getString(ConfigEnum.selectionText2.getPath()));
            teamSize1 = configuration.getInt(ConfigEnum.teamSize1.getPath());
            teamSize2 = configuration.getInt(ConfigEnum.teamSize2.getPath());
            onlyWizards = configuration.getBoolean(ConfigEnum.onlyWizards.getPath());


            return true;
        } catch (Throwable t) {
            ErrorHandle.error(ErrorHandle.cs, t, awv.getPrefix());
            return false;
        }

    }

    public boolean isDagiPlay() {
        return dagiPlay;
    }

    public enum ConfigEnum {
        SERVER_NAME ("server.serverName"),
        isEnabled ("settings.enable"),
        dagiPlay("settings.dagiPlay"),
        eventName("settings.eventName"),
        denyFormerCandidates("settings.denyFormerCandidates"),
        secondTeam("settings.secondTeam"),
        selectionText1("settings.selectionText1"),
        selectionText2("settings.selectionText2"),
        teamSize1("settings.teamSize1"),
        teamSize2("settings.teamSize2"),
        onlyWizards("settings.onlyWizards");

        private final String path;

        ConfigEnum(String s) {
            this.path = s;
        }

        public String getPath() {
            return path;
        }
    }

    public static Configuration getInstance() {
        return instance;
    }

    public String getSERVER_NAME() {
        return SERVER_NAME;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getEventName() {
        return eventName;
    }

    public boolean isDenyFormerCandidates() {
        return denyFormerCandidates;
    }

    public boolean isSecondTeam() {
        return secondTeam;
    }

    public String getSelectionText1() {
        return selectionText1;
    }

    public String getSelectionText2() {
        return selectionText2;
    }

    public int getTeamSize1() {
        return teamSize1;
    }

    public int getTeamSize2() {
        return teamSize2;
    }

    public boolean isOnlyWizards() {
        return onlyWizards;
    }
}
