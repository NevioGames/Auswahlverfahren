package de.neviogames.auswahlverfahren.utils;

import de.neviogames.auswahlverfahren.main.awv;
import de.neviogames.nglib.utils.io.ErrorHandle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

public final class Configuration {

    private static final Configuration instance = new Configuration();

    private String SERVER_NAME;
    private String SERVER_SCREEN;
    private boolean SILENT_ERROR;

    private boolean isEnabled;
    private boolean dagiPlay;


    public boolean load() {
        try {
            FileConfiguration configuration = awv.getPlugin(awv.class).getConfig();
            SERVER_NAME = configuration.getString(ConfigEnum.SERVER_NAME.getPath());
            SERVER_SCREEN = configuration.getString(ConfigEnum.SERVER_SCREEN.getPath());
            SILENT_ERROR = configuration.getBoolean(ConfigEnum.SILENT_ERROR.getPath());

            isEnabled = configuration.getBoolean(ConfigEnum.isEnabled.getPath());
            dagiPlay = configuration.getBoolean(ConfigEnum.dagiPlay.getPath());

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
        SERVER_SCREEN ("server.serverScreen"),
        SILENT_ERROR ("settings.silentErrors"),
        dagiPlay("settings.dagiPlay"),
        isEnabled ("settings.enable");

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

    public String getSERVER_SCREEN() {
        return SERVER_SCREEN;
    }

    public boolean isSILENT_ERROR() {
        return SILENT_ERROR;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

}
