package de.neviogames.auswahlverfahren.commands;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.utils.House;
import de.neviogames.auswahlverfahren.utils.database;
import de.neviogames.auswahlverfahren.utils.edits.argument;
import de.neviogames.auswahlverfahren.utils.edits.command;
import de.neviogames.auswahlverfahren.utils.edits.perm;
import de.neviogames.nglib.utils.io.sendMessage;
import de.neviogames.nglib.utils.utility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinCommand implements CommandExecutor, TabCompleter {

    public JoinCommand() {
        awv.getInstance().getCommand(command.joinEvent).setExecutor(this);
        awv.getInstance().getCommand(command.joinEvent).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Bewerbe dich fuer das Event");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.joinEvent, new String[][]{{argument.Contact}}));

        //EXECUTE COMMAND
        // Check command
        if(cmd.getName().equalsIgnoreCase(command.joinEvent)) {

            // Check is Plugin in config enabled
            if (!Configuration.getInstance().isEnabled()) {
                sender.sendMessage(awv.getPrefix() + "Es gibt zurzeit kein Event.");
            }

            // Check sender has permission
            if (!sender.hasPermission(perm.joinEvent)) {
                sendMessage.noPerm(sender, awv.getPrefix());
                return true;
            }

            // Check sender is Player
            if (!(sender instanceof Player)) {
                sendMessage.noPlayer(sender, awv.getPrefix());
                return true;
            }

            Player player = (Player) sender;

            // Check for wizards
            if (Configuration.getInstance().isOnlyWizards()) {
                if (!utility.isWizard(player)) {
                    sender.sendMessage(awv.getPrefix() + "Du musst eine Hexe oder ein Zauberer sein um an dem Event teilnehmen zu k" + utility.oe + "nnen.");
                    return true;
                }
            }

            // Check for previous participation
            if (Configuration.getInstance().isDenyFormerCandidates()) {
                if (database.isFormerCandidate(player.getUniqueId())) {
                    sender.sendMessage(awv.getPrefix() + "Du kannst dich leider für dieses Event nicht bewerben, da du zuvor schon an einem Event(" + database.getFormerCandidateEvent(player.getUniqueId()) + ") teilgenommen hast.");
                    return true;
                }
            }

            // Check argument length
            if (args.length == 0) {
                sender.sendMessage(awv.getPrefix() + "Bitte gebe eine Kontaktm" + utility.oe + "glichkeit an.");
                return false;
            }

            // Has the player already applied
            if (database.exist(player.getUniqueId())) {
                sender.sendMessage(awv.getPrefix() + "Du hast dich bereits beworben.");
                return true;
            }

            // put all arguments to a string
            StringBuilder builder = new StringBuilder();
            for (String a : args) {
                builder.append(a).append(" ");
            }

            String contact = builder.toString().trim();
            House house = House.fromName(database.getPlayerHouse(player.getUniqueId()));
            UUID uuid = player.getUniqueId();

            // check house is not null
            if (house == null) {
                sender.sendMessage(awv.getPrefix() + "Es ist ein Fehler aufgetreten. Bitte wende dich an ein Teammitglied.");
                return true;
            }

            // put player into database, send success messages
            database.add(uuid, house, contact);
            sender.sendMessage(awv.getPrefix() + "Du hast dich erfolgreich als Teilnehmer für das Haus "+house.getColor()+house.getDisplayName()+ ChatColor.GRAY+" beworben.");
            awv.getInstance().getLogger().info(uuid.toString() + " hat sich beim Event f"+utility.ue+"r das Haus " + house.name() + " beworben");

        }
        return true;
    }


    //EXECUTE TAB-COMPLETE
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase(command.joinEvent) || !sender.hasPermission(perm.joinEvent)) return Collections.emptyList();
        List<String> completions = new java.util.ArrayList<>();
        List<String> nextArgs = new ArrayList<>();

        if (args.length == 1) {
            nextArgs.add(sender.getName());
            StringUtil.copyPartialMatches(args[0], nextArgs, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}
