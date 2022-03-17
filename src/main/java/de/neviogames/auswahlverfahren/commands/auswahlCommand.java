package de.neviogames.auswahlverfahren.commands;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.auswahlverfahren.utils.*;
import de.neviogames.auswahlverfahren.utils.edits.argument;
import de.neviogames.auswahlverfahren.utils.edits.command;
import de.neviogames.auswahlverfahren.utils.edits.perm;
import de.neviogames.nglib.utils.io.MathUtil;
import de.neviogames.nglib.utils.io.sendMessage;
import de.neviogames.nglib.utils.misc.FormatUtil;
import de.neviogames.nglib.utils.utility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.auswahl, new String[][]{{argument.Gryffindor, argument.Hufflepuff, argument.Ravenclaw, argument.Slytherin}, {"[RandomZahl]"}}));

        //EXECUTE COMMAND
        // Check command
        if (cmd.getName().equalsIgnoreCase(command.auswahl)) {

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

            // Check is Plugin in config enabled
            if (!Configuration.getInstance().isEnabled()) {
                sender.sendMessage(awv.getPrefix() + "Es gibt zurzeit kein Event.");
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
            House house = House.fromName(args[0]);
            if (house == null) {
                sender.sendMessage(awv.getPrefix()+ ChatColor.RED + "Das Haus " +ChatColor.GOLD+ args[0] +ChatColor.RED+ " existiert nicht.");
                return false;
            }

            // Check argument 1 is a number
            if (!utility.isNumber(args[1])) {
                sendMessage.noNumber(sender, args[1], awv.getPrefix());
                return true;
            }

            int eingabeZahl = Integer.parseInt(args[1]);

            // Get a randomized list from a house and check size
            ArrayList<UUID> randomList = randomizer.randomList(house, eingabeZahl);
            if (randomList.size() < 8) {
                sender.sendMessage(awv.getPrefix() + ChatColor.RED+ "Es haben sich nicht genug Spieler im Haus " +house.getColor()+ house.name() +ChatColor.RED+ " beworben.");
                return true;
            }

            // Get a random candidate from the randomized list and build a String
            String text1 = Configuration.getInstance().getSelectionText1().replace("%houseColor", house.getColor().toString()).replace("%house", house.name());
            StringBuilder page1 = new StringBuilder(text1 + "\n\n");

            boolean isDagilp;
            for (int i = 1; i < Configuration.getInstance().getTeamSize1() + 1; i++) {
                UUID candidateUUID = randomList.get(MathUtil.fairRoundedRandom(0, randomList.size()));
                String candidateName = utility.getNameFromUUID(candidateUUID);

                isDagilp = false;
                if (i==1 && house.name().equals(House.HUFFLEPUFF.name()) && Configuration.getInstance().isDagiPlay()) {
                    candidateName = "DaGiLP";
                    isDagilp = true;
                }

                page1.append(ChatColor.BLACK).append(ChatColor.BOLD).append(i).append(". ").append(house.getColor()).append(candidateName).append("\n");
                if (isDagilp) continue;

                // remove candidate from the randomized list and whitelist the player for the event
                randomList.remove(candidateUUID);
                database.setWhitelist(candidateUUID,1);
            }

            // save and print the String with the candidates
            saveCandidates(page1.toString(), 1);
            awv.getInstance().getLogger().info(FormatUtil.stripFormat(page1.toString()));

            // create an output Book
            BookAPI book = new BookAPI();
            book.setAuthor(ChatColor.AQUA+"Auswahlverfahren");
            book.setDisplayName(""+house.getColor()+ChatColor.BOLD+house.name());
            book.setTitle("Die Auserw"+utility.ae+"hlten von " +house.getColor()+ house.name());
            book.setPages(page1.toString());

            // If seconds team = true, Get a random candidate from the randomized list a build a String
            if (Configuration.getInstance().isSecondTeam()) {
                String text2 = Configuration.getInstance().getSelectionText2().replace("%houseColor", house.getColor().toString()).replace("%house", house.name());
                StringBuilder page2 = new StringBuilder(text2 + "\n\n");

                for (int i = 1; i < Configuration.getInstance().getTeamSize2() + 1; i++) {
                    UUID candidateUUID = randomList.get(MathUtil.fairRoundedRandom(0, randomList.size()));
                    String candidateName = utility.getNameFromUUID(candidateUUID);
                    page2.append(ChatColor.BLACK).append(ChatColor.BOLD).append(i).append(". ").append(house.getColor()).append(candidateName).append("\n");

                    //remove candidate from the randomized list and whitelist the player for the event
                    randomList.remove(candidateUUID);
                    database.setWhitelist(candidateUUID, 2);
                }

                // save and print the String with the candidates, add a new page to the book
                book.addPage(page2.toString());
                saveCandidates(page2.toString(), 2);
                awv.getInstance().getLogger().info(FormatUtil.stripFormat(page2.toString()));
            }

            // give the book to the player
            for (int i = 0; i < 9; i++) {
                if(player.getInventory().getItem(i) == null) {
                    player.getInventory().setItem(i, book.create());
                    return true;
                }
            }
            sender.sendMessage(awv.getPrefix() + ChatColor.RED + ChatColor.BOLD + "Leere deine Inventarleiste aus! Befehl konnte nicht ausgef"+utility.ue+"hrt werden.");

        }
        return false;
    }

    // save candidates in a file
    private static void saveCandidates(String candidateString, int team) {
        String saveText = new SimpleDateFormat("hh:mm:ss").format(System.currentTimeMillis()) + " - Team: "+team+" \n" + candidateString + "\n\n";
        try {
            String save = FormatUtil.stripFormat(saveText);
            save += "\n\n";

            FileWriter outStream = new FileWriter(awv.getInstance().getDataFolder()+"/candidates.txt", true);
            BufferedWriter out = new BufferedWriter(outStream);
            out.append(save.replaceAll("\n", System.getProperty("line.separator")));
            out.close();
            outStream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    //EXECUTE TAB-COMPLETE
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase(command.auswahl) || !sender.hasPermission(perm.auswahl)) return Collections.emptyList();
        List<String> completions = new ArrayList<>();
        List<String> nextArgs = new ArrayList<>();

        if(args.length == 1) {
            if(!Configuration.getInstance().isOnlyWizards()) nextArgs.add(argument.Muggle);
            nextArgs.add(argument.Gryffindor);
            nextArgs.add(argument.Hufflepuff);
            nextArgs.add(argument.Slytherin);
            nextArgs.add(argument.Ravenclaw);

            StringUtil.copyPartialMatches(args[0], nextArgs, completions);
        } else if(args.length == 2) {
            for (int i = 1; i < 15; i++) {
                nextArgs.add(String.valueOf(i));
            }
            StringUtil.copyPartialMatches(args[1], nextArgs, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}