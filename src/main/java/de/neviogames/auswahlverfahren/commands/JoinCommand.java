package de.neviogames.auswahlverfahren.commands;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.auswahlverfahren.utils.*;
import de.neviogames.auswahlverfahren.edits.argument;
import de.neviogames.auswahlverfahren.edits.command;
import de.neviogames.auswahlverfahren.edits.perm;
import de.neviogames.nglib.utils.io.sendMessage;
import de.neviogames.nglib.utils.utility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.*;

public class JoinCommand implements CommandExecutor, TabCompleter {

    public JoinCommand() {
        awv.getInstance().getCommand(command.joinEvent).setExecutor(this);
        awv.getInstance().getCommand(command.joinEvent).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Bewerbe dich fuer das Event");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.joinEvent, new String[][]{{argument.Contact}}));

        // EXECUTE COMMAND
        // Check command
        if (cmd.getName().equalsIgnoreCase(command.joinEvent)) return false;

        // Check is Plugin in config enabled
        if (!Configuration.getInstance().isApplicationPhase()) {
            sender.sendMessage(awv.getPrefix() + "Es gibt zurzeit kein Event Bewerbungsphase.");
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
        UUID uuid = player.getUniqueId();

        // Check for previous participation
        if (Configuration.getInstance().isDenyFormerCandidates()) {
            if (database.isFormerCandidate(uuid)) {
                sender.sendMessage(awv.getPrefix() + "Du kannst dich leider f"+utility.ue+"r dieses Event nicht bewerben, da du zuvor schon an einem Event(" + database.getFormerCandidateEvent(uuid) + ") teilgenommen hast.");
                return true;
            }
        }

        // Check for forbidden players
        if (Configuration.getInstance().getForbiddenPlayers().contains(uuid)) {
            sender.sendMessage(awv.getPrefix() + "Du kannst dich leider f"+utility.ue+"r dieses Event nicht bewerben, da du zum Event-Team geh"+utility.oe+"rst.");
            return true;
        }

        // Check argument length
        if (args.length == 0) {
            sender.sendMessage(awv.getPrefix() + "Bitte gebe eine Kontaktm" + utility.oe + "glichkeit an.");
            return false;
        }

        // Has the player already applied
        if (database.exist(uuid)) {
            sender.sendMessage(awv.getPrefix() + "Du hast dich bereits beworben.");
            return true;
        }

        // Put all arguments to a string
        StringBuilder builder = new StringBuilder();
        for (String a : args) {
            builder.append(a).append(" ");
        }

        NGEventCandidate candidate = new NGEventCandidate(player, builder.toString().trim(), NGEventTeam.getTeam(player));

        if (candidate.getTeam() == null) {
            //TODO OPEN SELECT TEAM INVENTORY (and remove error message)
            sender.sendMessage(awv.getPrefix() + "Es ist ein Fehler aufgetreten. Bitte wende dich an ein Teammitglied.");
            return true;
        }

        //TODO OPEN CONFIRMATION INVENTORY

        // Put player into database, send success messages
        database.add(candidate);
        sender.sendMessage(awv.getPrefix() + "Du hast dich erfolgreich als Teilnehmer für das Haus "+candidate.getTeam().getColor()+candidate.getTeam().getDisplayName()+ ChatColor.GRAY+" beworben.");
        awv.getInstance().getLogger().info(uuid + " hat sich beim Event f"+utility.ue+"r das Haus " + candidate.getTeam().getDisplayName() + " beworben");
        return true;
    }


    // EXECUTE TAB-COMPLETE
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
