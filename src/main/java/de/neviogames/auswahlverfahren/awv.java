package de.neviogames.auswahlverfahren;

import de.neviogames.auswahlverfahren.commands.JoinCommand;
import de.neviogames.auswahlverfahren.commands.LeaveCommand;
import de.neviogames.auswahlverfahren.commands.auswahlCommand;
import de.neviogames.auswahlverfahren.commands.reloadCommand;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.utils.database;
import de.neviogames.auswahlverfahren.utils.edits.command;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class awv extends JavaPlugin {

    @Getter
    private static final String prefix = ChatColor.DARK_AQUA + "[" + ChatColor.DARK_RED + "N" + ChatColor.GOLD + "G" + ChatColor.DARK_AQUA + "] " + ChatColor.GRAY;

    @Getter
    private static awv instance;

    private final String name = this.getDescription().getName();
    private final String version = this.getDescription().getVersion();
    private final List<String> authors = this.getDescription().getAuthors();

    @Override
    public void onEnable() {
        getLogger().info("-------------------------------");
        getLogger().info("Lade "+name+"");
        instance = this;

        //Load Config
        saveDefaultConfig();
        if(Configuration.getInstance().load()) {
            getLogger().info("Config geladen.");
        } else getLogger().warning("Config wurde nicht geladen!");

        //Load MySQL
        database.createTable();
        database.createTableFormerEventCandidates();

        //Load Commands
        registerCMD();

        getLogger().info("Plugin " + name + " Version " + version + " erfolgreich geladen");
        getLogger().info("by " + authors.toString().replace("[","").replace("]",""));
        getLogger().info("Plugin Aktiviert");
        getLogger().info("-------------------------------");
    }

    @Override
    public void onDisable() {
        getLogger().info("-------------------------------");
        instance = null;
        getServer().getServicesManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        getLogger().info("Plugin " + name + " Version " + version + " entladen");
        getLogger().info("by " + authors.toString().replace("[","").replace("]",""));
        getLogger().info("Plugin Deaktiviert");
        getLogger().info("-------------------------------");
    }

    private void registerCMD() {
        new auswahlCommand();
        new reloadCommand();
        new JoinCommand();
        new LeaveCommand();
    }
}
