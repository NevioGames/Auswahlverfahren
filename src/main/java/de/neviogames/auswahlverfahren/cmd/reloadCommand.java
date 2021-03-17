package de.neviogames.auswahlverfahren.cmd;

import de.neviogames.auswahlverfahren.main.awv;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.utils.edits.argument;
import de.neviogames.auswahlverfahren.utils.edits.command;
import de.neviogames.auswahlverfahren.utils.edits.perm;
import de.neviogames.nglib.utils.io.sendMessage;
import de.neviogames.nglib.utils.utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class reloadCommand implements CommandExecutor {
  @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Ladet die Konfik neu");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.awv, new String[][]{{argument.reload}}));

        //EXECUTE COMMAND
        if (cmd.getName().equalsIgnoreCase(command.awv)) {

               if (sender.hasPermission(perm.reload)) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (utility.isAdmin(p) && utility.isWizard(p)) {
                            if (args.length == 1) {
                              if (args [0].equalsIgnoreCase(argument.reload)){
                                    awv.getPlugin(awv.class).reloadConfig();
                                    Configuration.getInstance().load();
                                    sender.sendMessage(awv.getPrefix() + "Konfik wurde neu geladen");
                                    return true;
                                    }else {
                                  sendMessage.wrongArgs(sender, awv.getPrefix());
                              }
                                }else {
                                    sendMessage.wrongArgs(sender, awv.getPrefix()); }
                            return false;
                        } else {
                            sender.sendMessage(awv.getPrefix()+ "Du musst Admin sein um das Ausf"+utility.ue+"hren zu k"+utility.oe+"nnen");
                        }
                    } else{
                        sendMessage.noPlayer(sender, awv.getPrefix());
                    }
                } else {
                   sendMessage.noPerm(sender, awv.getPrefix());
               }
            return true;
        }
        return false;
    }
}
