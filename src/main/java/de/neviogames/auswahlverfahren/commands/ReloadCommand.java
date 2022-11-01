package de.neviogames.auswahlverfahren.commands;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.edits.argument;
import de.neviogames.auswahlverfahren.edits.command;
import de.neviogames.auswahlverfahren.edits.perm;
import de.neviogames.nglib.utils.io.sendMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    public ReloadCommand() {
        awv.getInstance().getCommand(command.awv).setExecutor(this);
        awv.getInstance().getCommand(command.awv).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Ladet die Config neu");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.awv, new String[][]{{argument.reload}}));

        // EXECUTE COMMAND
        // Check command
        if (!cmd.getName().equalsIgnoreCase(command.awv)) return false;

        // Check sender has permission
        if (!sender.hasPermission(perm.reload)) {
            sendMessage.noPerm(sender, awv.getPrefix());
            return true;
        }

        // Check argument length
        if (args.length == 0) {
            sendMessage.lessArgs(sender, awv.getPrefix());
            return false;
        }

        if (args.length > 1) {
            sendMessage.manyArgs(sender, awv.getPrefix());
            return false;
        }

        // Check the argument and reload config
        if (args[0].equalsIgnoreCase(argument.reload)) {
            awv.getPlugin(awv.class).reloadConfig();
            Configuration.getInstance().load();
            sender.sendMessage(awv.getPrefix() + "Config wurde neu geladen");
            return true;

        } else {
            sendMessage.wrongArgs(sender, awv.getPrefix());
            return false;
        }
    }

    // EXECUTE TAB-COMPLETE
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase(command.awv) || !sender.hasPermission(perm.reload)) return Collections.emptyList();
        List <String> completions = new ArrayList<>();
        List <String> nextArgs = new ArrayList<>();

        if (args.length == 1) {
            nextArgs.add(argument.reload);
            StringUtil.copyPartialMatches(args[0], nextArgs, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}
