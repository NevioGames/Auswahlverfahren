package de.neviogames.auswahlverfahren.commands;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.auswahlverfahren.utils.Configuration;
import de.neviogames.auswahlverfahren.utils.NGEventSelectHandler;
import de.neviogames.auswahlverfahren.utils.NGEventTeam;
import de.neviogames.auswahlverfahren.utils.Util;
import de.neviogames.auswahlverfahren.edits.command;
import de.neviogames.auswahlverfahren.edits.perm;
import de.neviogames.nglib.utils.io.sendMessage;
import de.neviogames.nglib.utils.utility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class auswahlCommand implements CommandExecutor, TabCompleter {

    public auswahlCommand() {
        awv.getInstance().getCommand(command.auswahl).setExecutor(this);
        awv.getInstance().getCommand(command.auswahl).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Ziehe die Teilnehmer");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.auswahl, new String[][]{Configuration.getInstance().getTeams().keySet().toArray(new String[0]), {"[RandomZahl]"}}));

        //EXECUTE COMMAND
        // Check command
        if (!cmd.getName().equalsIgnoreCase(command.auswahl)) return false;

        // Check sender has permission
        if (!sender.hasPermission(perm.auswahl)) {
            sendMessage.noPerm(sender, awv.getPrefix());
            return true;
        }

        // Check sender is Player
        if (!(sender instanceof Player)) {
            sendMessage.noPlayer(sender, awv.getPrefix());
            return true;
        }

        // Check is Player Admin
        Player player = (Player) sender;
        if (!utility.isAdmin(player)) {
            sender.sendMessage(awv.getPrefix() + "Du musst ein Admin sein, um diesen Befehl auszuf"+utility.ue+"rehen.");
            return true;
        }

        // Check argument length
        if (args.length < 2) {
            sendMessage.lessArgs(sender, awv.getPrefix());
            return false;
        }

        if (args.length > 2) {
            sendMessage.manyArgs(sender, awv.getPrefix());
            return false;
        }

        // Get House and check is not null
        if (Configuration.getInstance().getTeams() == null) {
            sender.sendMessage(awv.getPrefix()+ ChatColor.RED + "Es sind keine Teams geladen.");
            return true;
        }

        final boolean all = args[0].equals("*");
        NGEventTeam team = null;
        if (!all) {
            team = Configuration.getInstance().getTeams().get(args[0]);
            if (team == null) {
                sender.sendMessage(awv.getPrefix() + ChatColor.RED + "Das Team " + ChatColor.GOLD + args[0] + ChatColor.RED + " existiert nicht.");
                return false;
            }
        }

        // Check argument 1 is a number
        if (!utility.isNumber(args[1])) {
            sendMessage.noNumber(sender, args[1], awv.getPrefix());
            return true;
        }

        final int inputNumber = Integer.parseInt(args[1]);


        if (all) {
            if (Util.getFreeSlots(player.getInventory()) < Configuration.getInstance().getTeams().size()) {
                sender.sendMessage(awv.getPrefix() + ChatColor.RED + ChatColor.BOLD + "Leere dein Inventar aus!");
                return true;
            }

            Configuration.getInstance().getTeams().values().forEach(t -> {
                NGEventSelectHandler handler = new NGEventSelectHandler(sender, t, inputNumber);
                if (!handler.hasTeamEnoughPlayer()) return;
                player.getInventory().addItem(handler.getBook(handler.select()));
            });

            player.updateInventory();
            return true;
        }

        if (Util.getFreeSlots(player.getInventory()) < 1) {
            sender.sendMessage(awv.getPrefix() + ChatColor.RED + ChatColor.BOLD + "Leere dein Inventar aus!");
            return true;
        }

        NGEventSelectHandler handler = new NGEventSelectHandler(sender, team, inputNumber);
        if (!handler.hasTeamEnoughPlayer()) return true;
        player.getInventory().addItem(handler.getBook(handler.select()));
        player.updateInventory();

        return true;
    }

    //EXECUTE TAB-COMPLETE
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase(command.auswahl) || !sender.hasPermission(perm.auswahl)) return Collections.emptyList();
        List<String> completions = new ArrayList<>();
        List<String> nextArgs = new ArrayList<>();

        if (args.length == 1) {
            nextArgs.addAll(Configuration.getInstance().getTeams().keySet());
            StringUtil.copyPartialMatches(args[0], nextArgs, completions);
        } else if (args.length == 2) {
            for (int i = 1; i < 100; i++) {
                nextArgs.add(String.valueOf(i));
            }
            StringUtil.copyPartialMatches(args[1], nextArgs, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}