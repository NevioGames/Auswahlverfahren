package de.neviogames.auswahlverfahren.cmd;

import de.neviogames.auswahlverfahren.main.awv;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.utils.database;
import de.neviogames.auswahlverfahren.utils.edits.command;
import de.neviogames.auswahlverfahren.utils.edits.perm;
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Trage dich aus dem Event aus");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.leaveEvent, new String[][]{{}}));

        //EXECUTE COMMAND
        if(cmd.getName().equalsIgnoreCase(command.leaveEvent)) {
            if(Configuration.getInstance().isEnabled()) {
                if(sender.hasPermission(perm.leaveEvent)) {
                    if(sender instanceof Player) {
                        Player p = (Player) sender;
                        if(args.length != 0) {
                            sendMessage.manyArgs(sender, awv.getPrefix());
                            return false;
                        }

                        if(database.exist(p.getUniqueId())) {
                            UUID uuid = p.getUniqueId();

                            database.remove(uuid);
                            sender.sendMessage(awv.getPrefix() + "Du hast dich erfolgreich aus dem Event ausgetragen.");
                            awv.getInstance().getLogger().info(uuid.toString() + " hat sich aus dem Event ausgetragen.");
                        } else {
                            sender.sendMessage(awv.getPrefix() + "Du nimmst nicht am Event teil.");
                        }

                    } else {
                        sendMessage.noPlayer(sender, awv.getPrefix());
                    }
                } else {
                    sendMessage.noPerm(sender, awv.getPrefix());
                }
            } else {
                sender.sendMessage(awv.getPrefix() + "Es gibt zurzeit kein Event.");
            }
            return true;
        }

        return false;
    }


    //EXECUTE TAB-COMPLETE
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
       return Collections.emptyList();
    }
}
