package de.neviogames.auswahlverfahren.commands;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.utils.database;
import de.neviogames.auswahlverfahren.edits.command;
import de.neviogames.auswahlverfahren.edits.perm;
import de.neviogames.nglib.utils.io.sendMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LeaveCommand implements CommandExecutor, TabCompleter {

    public LeaveCommand() {
        awv.getInstance().getCommand(command.leaveEvent).setExecutor(this);
        awv.getInstance().getCommand(command.leaveEvent).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Trage dich aus dem Event aus");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.leaveEvent, new String[][]{{}}));

        // EXECUTE COMMAND
        // Check command
        if (!cmd.getName().equalsIgnoreCase(command.leaveEvent)) return false;

        // Check is Plugin in config enabled
        if (!Configuration.getInstance().isApplicationPhase()) {
            sender.sendMessage(awv.getPrefix() + "Es gibt zurzeit keine Event Bewerbungsphase.");
        }

        // Check sender has permission
        if (!sender.hasPermission(perm.leaveEvent)) {
            sendMessage.noPerm(sender, awv.getPrefix());
            return true;
        }

        // Check sender is Player
        if (!(sender instanceof Player)) {
            sendMessage.noPlayer(sender, awv.getPrefix());
            return true;
        }

        // Check argument length
        if (args.length != 0) {
            sendMessage.manyArgs(sender, awv.getPrefix());
            return false;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        // Has the player already applied
        if (!database.exist(uuid)) {
            sender.sendMessage(awv.getPrefix() + "Du nimmst nicht am Event teil.");
            return true;
        }

        // remove player from database
        database.remove(uuid);
        sender.sendMessage(awv.getPrefix() + "Du hast dich erfolgreich aus dem Event ausgetragen.");
        awv.getInstance().getLogger().info(uuid + " hat sich aus dem Event ausgetragen.");
        return true;
    }


    // EXECUTE TAB-COMPLETE
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
