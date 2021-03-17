package de.neviogames.auswahlverfahren.cmd;

import de.neviogames.auswahlverfahren.main.awv;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.utils.House;
import de.neviogames.auswahlverfahren.utils.database;
import de.neviogames.auswahlverfahren.utils.edits.argument;
import de.neviogames.auswahlverfahren.utils.edits.command;
import de.neviogames.auswahlverfahren.utils.edits.perm;
import de.neviogames.nglib.utils.io.sendMessage;
import de.neviogames.nglib.utils.utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JoinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Bewerbe dich fuer das Event");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.joinEvent, new String[][]{{argument.Contact}}));

        //EXECUTE COMMAND
        if(cmd.getName().equalsIgnoreCase(command.joinEvent)) {
            if(Configuration.getInstance().isEnabled()) {
                if(sender.hasPermission(perm.joinEvent)) {
                    if(sender instanceof Player) {
                        Player p = (Player) sender;
                        if(utility.isWizard(p)) {
                            if(args.length == 0) {
                                sender.sendMessage(awv.getPrefix() + "Bitte gebe eine Kontaktm"+utility.oe+"glichkeit an.");
                                return false;
                            }

                            if(!database.exist(p.getUniqueId())) {
                                StringBuilder builder = new StringBuilder();
                                for(String a : args) {
                                    builder.append(a).append(" ");
                                }

                                String contact = builder.toString().trim();
                                House house = House.fromName(database.getHouse(p.getUniqueId()));
                                UUID uuid = p.getUniqueId();

                                if(house==null) {
                                    sender.sendMessage(awv.getPrefix() + "Es ist ein Fehler aufgetreten. Bitte wende dich an ein Teammitglied.");
                                    return true;
                                }

                                database.add(uuid, house, contact);
                                sender.sendMessage(awv.getPrefix() + "Du hast dich erfolgreich beworben.");
                                awv.getInstance().getLogger().info(uuid.toString() + " hat sich beim Event fuer das Haus " +house+ " beworben");
                            } else {
                                sender.sendMessage(awv.getPrefix() + "Du hast dich bereits beworben.");
                            }

                        } else {
                            sender.sendMessage(awv.getPrefix() + "Du musst ein Zauberer sein, um an dem Event teilzunehmen.");
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


}
