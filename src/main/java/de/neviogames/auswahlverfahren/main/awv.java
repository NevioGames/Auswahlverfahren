package de.neviogames.auswahlverfahren.main;

import de.neviogames.auswahlverfahren.cmd.JoinCommand;
import de.neviogames.auswahlverfahren.cmd.LeaveCommand;
import de.neviogames.auswahlverfahren.cmd.auswahlCommand;
import de.neviogames.auswahlverfahren.cmd.reloadCommand;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.utils.database;
import de.neviogames.auswahlverfahren.utils.edits.command;
import de.neviogames.nglib.utils.io.ErrorHandle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class awv extends JavaPlugin {


    private static final String prefix = ChatColor.DARK_AQUA + "[" + ChatColor.DARK_RED + "N" + ChatColor.GOLD + "G" + ChatColor.DARK_AQUA + "] " + ChatColor.GRAY;

    private static awv instance;

    private final String name = this.getDescription().getName();
    private final String version = this.getDescription().getVersion();
    private final List<String> authors = this.getDescription().getAuthors();

    @Override
    public void onEnable() {
        getLogger().info("-------------------------------");
        saveDefaultConfig();
        getLogger().info("Lade "+name+"");
        instance = this;
        if(Configuration.getInstance().load()) {
            getLogger().info("Config geladen.");
        } else getLogger().warning("Config wurde nicht geladen!");
        database.createTable();
        try {
            registerCMD();
        } catch (Throwable t) {
            ErrorHandle.error(ErrorHandle.cs, t, prefix);
        }
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
        getCommand(command.auswahl).setExecutor(new auswahlCommand());
        getCommand(command.auswahl).setTabCompleter(new auswahlCommand());
        getCommand(command.awv).setExecutor(new reloadCommand());
        getCommand(command.awv).setTabCompleter(new reloadCommand());
        getCommand(command.joinEvent).setExecutor(new JoinCommand());
        getCommand(command.joinEvent).setTabCompleter(new JoinCommand());
        getCommand(command.leaveEvent).setExecutor(new LeaveCommand());
        getCommand(command.leaveEvent).setTabCompleter(new LeaveCommand());

    }

    public static String getPrefix() {
        return prefix;
    }

    public static awv getInstance() {
        return instance;
    }
}
