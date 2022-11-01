package de.neviogames.auswahlverfahren;

import de.neviogames.auswahlverfahren.commands.JoinCommand;
import de.neviogames.auswahlverfahren.commands.LeaveCommand;
import de.neviogames.auswahlverfahren.commands.auswahlCommand;
import de.neviogames.auswahlverfahren.commands.ReloadCommand;
import de.neviogames.auswahlverfahren.listener.InventoryClick;
import de.neviogames.auswahlverfahren.listener.InventoryClose;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.utils.database;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
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
    public void onLoad() {
        getLogger().info("-------------------------------");
        getLogger().info("Setting up " +this.name);
        instance = this;


        getLogger().info("Loading configurations...");
        // Load configuration
        loadConfiguration();

        getLogger().info("-------------------------------");
    }

    @Override
    public void onEnable() {
        getLogger().info("-------------------------------");
        getLogger().info("Lade "+this.name+"");

        //Load MySQL
        createMysqlTables();

        //Load Commands
        registerCommands();

        //Load Listener
        registerListener();

        logEndMessage("Loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("-------------------------------");
        instance = null;
        getServer().getServicesManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        logEndMessage("Unloaded");
    }

    private void logEndMessage(String message) {
        getLogger().info("Plugin " + this.name + " Version " + this.version + " " + message);
        getLogger().info("by " + this.authors.toString().replace("[","").replace("]",""));
        getLogger().info("-------------------------------");
    }

    public void loadConfiguration() {
        saveDefaultConfig();
        if(Configuration.getInstance().load()) {
            getLogger().info("Config geladen.");
        } else {
            getLogger().warning("Config wurde nicht geladen!");
        }
    }

    public void createMysqlTables() {
        database.createTable();
        database.createTableFormerEventCandidates();
    }

    private void registerCommands() {
        new auswahlCommand();
        new ReloadCommand();
        new JoinCommand();
        new LeaveCommand();
    }

    private void registerListener() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new InventoryClose(), this);
    }
}
