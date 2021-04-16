package de.neviogames.auswahlverfahren.cmd;

import de.neviogames.auswahlverfahren.main.awv;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.utils.edits.argument;
import de.neviogames.auswahlverfahren.utils.edits.command;
import de.neviogames.auswahlverfahren.utils.edits.perm;
import de.neviogames.nglib.utils.io.sendMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class reloadCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Ladet die Config neu");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.awv, new String[][]{{argument.reload}}));

        //EXECUTE COMMAND
        // Check command, permission and argument length
        if (cmd.getName().equalsIgnoreCase(command.awv)) {
            if (sender.hasPermission(perm.reload)) {
                if (args.length == 1) {

                    // Check the argument and reload config
                    if (args [0].equalsIgnoreCase(argument.reload)) {
                        awv.getPlugin(awv.class).reloadConfig();
                        Configuration.getInstance().load();
                        sender.sendMessage(awv.getPrefix() + "Config wurde neu geladen");
                        return true;

                    } else {
                      sendMessage.wrongArgs(sender, awv.getPrefix());
                    }
                } else {
                    sendMessage.wrongArgs(sender, awv.getPrefix());
                }
                return false;
            } else {
                sendMessage.noPerm(sender, awv.getPrefix());
            }
            return true;
        }
        return false;
    }

    //EXECUTE TAB-COMPLETE
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase(command.awv)) {
            List <String> completions = new ArrayList<>();
            List <String> nextArgs = new ArrayList<>();
            if (sender.hasPermission(perm.reload)) {
                if (args.length==1) {
                    nextArgs.add(argument.reload);
                    StringUtil.copyPartialMatches(args[0], nextArgs,completions);
                }
                return completions;
            }
        }
        return Collections.emptyList();
    }
}
