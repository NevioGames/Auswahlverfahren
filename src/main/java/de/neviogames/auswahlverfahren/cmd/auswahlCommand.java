package de.neviogames.auswahlverfahren.cmd;

import de.neviogames.auswahlverfahren.main.awv;
import de.neviogames.auswahlverfahren.utils.BookAPI;
import de.neviogames.auswahlverfahren.utils.House;
import de.neviogames.auswahlverfahren.utils.edits.argument;
import de.neviogames.auswahlverfahren.utils.edits.command;
import de.neviogames.auswahlverfahren.utils.edits.perm;
import de.neviogames.auswahlverfahren.utils.randomizer;
import de.neviogames.nglib.utils.io.MathUtil;
import de.neviogames.nglib.utils.io.sendMessage;
import de.neviogames.nglib.utils.misc.FormatUtil;
import de.neviogames.nglib.utils.utility;
import de.neviogames.auswahlverfahren.utils.Configuration;
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Ziehe die Teilnehmer");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.auswahl, new String[][]{{argument.Gryffindor, argument.Hufflepuff, argument.Ravenclaw, argument.Slytherin}, {"[RandomZahl]"}}));

        //EXECUTE COMMAND
        if(cmd.getName().equalsIgnoreCase(command.auswahl)) {
            if(sender.hasPermission(perm.auswahl)) {
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    if(utility.isAdmin(p) && utility.isWizard(p)) {
                        if(!Configuration.getInstance().isEnabled()) {
                            sender.sendMessage(awv.getPrefix() + "Es gibt zurzeit kein Event.");
                        }

                        if(args.length < 2) {
                            sendMessage.lessArgs(sender, awv.getPrefix());
                            return false;
                        }

                        if(args.length == 2) {

                            House house = House.fromName(args[0]);
                            if(house==null) {
                                sender.sendMessage(awv.getPrefix()+ ChatColor.RED + "Das Haus " +ChatColor.GOLD+ args[0] +ChatColor.RED+ " existiert nicht.");
                                return false;
                            }

                            if(utility.isNumber(args[1])){
                                int eingabeZahl = Integer.parseInt(args[1]);

                                ArrayList<UUID> randomList = randomizer.randomList(house, eingabeZahl);
                                if(randomList.size() >= 8) {
                                    String s1 = Configuration.getInstance().getSelectionText1().replace("%houseColor", house.getColor().toString()).replace("%house", house.name());
                                    awv.getInstance().getLogger().info(FormatUtil.stripFormat(s1));
                                    StringBuilder page1 = new StringBuilder(s1 + "\n\n");
                                    for(int i = 1; i < Configuration.getInstance().getTeamSize1()+1; i++) {
                                        UUID kandidat = randomList.get(MathUtil.fairRoundedRandom(0, randomList.size()));
                                        String KandidatName = utility.getNameFromUUID(kandidat);
                                        if(i==1 && house.name().equals(House.Hufflepuff.name())&& Configuration.getInstance().isDagiPlay()) {
                                            KandidatName = "DaGiLP";
                                        }
                                        awv.getInstance().getLogger().info(i+". "+ KandidatName);
                                        page1.append(ChatColor.BLACK).append(ChatColor.BOLD).append(i).append(". ").append(house.getColor()).append(KandidatName).append("\n");
                                        randomList.remove(kandidat);
                                    }
                                    saveKandidaten(page1.toString(), 1);

                                    BookAPI book = new BookAPI();
                                    book.setAuthor(ChatColor.AQUA+"Auswahlverfahren");
                                    book.setDisplayName(""+house.getColor()+ChatColor.BOLD+house.name());
                                    book.setTitle("Die Auserw"+utility.ae+"hlten von " +house.getColor()+ house.name());
                                    book.setPages(page1.toString());

                                    if(Configuration.getInstance().isSecondTeam()) {
                                        String s2 = Configuration.getInstance().getSelectionText2().replace("%houseColor", house.getColor().toString()).replace("%house", house.name());
                                        awv.getInstance().getLogger().info(FormatUtil.stripFormat(s2));
                                        StringBuilder page2 = new StringBuilder(s2 + "\n\n");
                                        for (int i = 1; i < Configuration.getInstance().getTeamSize2()+1; i++) {
                                            UUID kandidat = randomList.get(MathUtil.fairRoundedRandom(0, randomList.size()));
                                            String KandidatName = utility.getNameFromUUID(kandidat);
                                            awv.getInstance().getLogger().info(i + ". " + KandidatName);
                                            page2.append(ChatColor.BLACK).append(ChatColor.BOLD).append(i).append(". ").append(house.getColor()).append(KandidatName).append("\n");
                                            randomList.remove(kandidat);
                                            database.setWhitelist(kandidat, 2);
                                        }
                                        book.addPage(page2.toString());
                                        saveKandidaten(page2.toString(), 2);
                                    }


                                    for (int i = 0; i < 9; i++) {
                                        if(p.getInventory().getItem(i) == null) {
                                            p.getInventory().setItem(i, book.create());
                                            return true;
                                        }
                                    }

                                    sender.sendMessage(awv.getPrefix() + ChatColor.RED + ChatColor.BOLD + "Leere deine Inventarleiste aus! Befehl konnte nicht ausgef"+utility.ue+"hrt werden.");

                                } else {
                                    sender.sendMessage(awv.getPrefix() + ChatColor.RED+ "Es haben sich nicht genug Spieler im Haus " +house.getColor()+ house.name() +ChatColor.RED+ " angemeldet.");
                                }

                            }
                            return true;
                        }
                        return false;
                    } else {
                        sender.sendMessage(awv.getPrefix() + "Du musst ein Admin sein, um diesen Befehl auszuf"+utility.ue+"rehen.");
                    }
                } else {
                    sendMessage.noPlayer(sender, awv.getPrefix());
                }
            } else {
                sendMessage.noPerm(sender, awv.getPrefix());
            }
            return true;
        }

        return false;
    }

    private static void saveKandidaten(String kandidaten, int team) {
        String s = new SimpleDateFormat("hh:mm:ss").format(System.currentTimeMillis()) + " - Team: "+team+" \n" + kandidaten + "\n\n";
        try {
            String save = FormatUtil.stripFormat(s);
            save += "\n\n";

            FileWriter outStream = new FileWriter("plugins/Auswahlverfahren/auswahl.txt", true);
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
        if(cmd.getName().equalsIgnoreCase(command.auswahl)) {
            List<String> completions = new ArrayList<>();
            List<String> nextArgs = new ArrayList<>();
            if(sender.hasPermission(perm.auswahl)) {
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
            }

            Collections.sort(completions);
            return completions;
        }
        return Collections.emptyList();
    }
}